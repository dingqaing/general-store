package com.me.generalstore.redis.dao.impl;

import com.me.generalstore.redis.model.article.Article;
import com.me.generalstore.redis.consts.RedisDataStructureKey;
import com.me.generalstore.redis.dao.ArticleDao;
import lombok.Setter;
import org.springframework.beans.BeanUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

/**
 * The type Article dao.
 *
 * @author dqq
 * @date 2021 /2/4
 */
@Setter
public class ArticleDaoImpl implements ArticleDao {

    private static final int VOTE_SCORE = 432;

    private static final int ONE_WEEK_IN_SECOND = 7 * 86400;

    private static final int ARTICLE_PER_PAGE = 25;

    private Jedis jedis;

    @Override
    public String postArticle(String user, String title, String link) {

        String articleId = jedis.incr("article:") + "";
        String voted = "voted:" + articleId;
        long now = LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8"));
        String article = "article:" + articleId;
        Article articleModel = new Article();
        Map<String, String> map = new HashMap<>(16);
        BeanUtils.copyProperties(articleModel, map);

        Transaction transaction = jedis.multi();
        jedis.sadd(voted, user);
        jedis.expire(voted, ONE_WEEK_IN_SECOND);
        jedis.hmset(article, map);
        jedis.zadd(RedisDataStructureKey.ARTICLE_TIME_ZSET, now, article);
        jedis.zadd(RedisDataStructureKey.ARTICLE_SCORE_ZSET, now + VOTE_SCORE, article);
        transaction.exec();
        return articleId;
    }

    @Override
    public List<Article> getArticle(int page, String order) {
        int start = (page - 1) * ARTICLE_PER_PAGE;
        int end = start + ARTICLE_PER_PAGE - 1;

        Set<String> articleIds = jedis.zrevrange(order, start, end);
        List<Article> list = new ArrayList<>(16);
        for (String articleId : articleIds) {
            Map<String, String> articleMap = jedis.hgetAll(articleId);
            Article article = new Article();
            BeanUtils.copyProperties(articleMap, article);
            list.add(article);
        }
        return list;
    }

    @Override
    public void voteArticle(String userId, String article) {
        // 计算文章投票截止时间
        long cutoff = LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8")) - ONE_WEEK_IN_SECOND;
        // 检查是否可对文章投票
        if (jedis.zscore(RedisDataStructureKey.ARTICLE_TIME_ZSET, article) < cutoff) {
            return;
        }
        // 取出文章 id
        String articleId = article.substring(article.indexOf(':') + 1);
        // 检查是否投过票，避免重复投票
        Transaction transaction = jedis.multi();
        if (jedis.sadd(RedisDataStructureKey.ARTICLE_USER_VOTE_SET + articleId, userId) == 1) {
            // 票数增加 423
            jedis.zincrby(RedisDataStructureKey.ARTICLE_SCORE_ZSET, VOTE_SCORE, articleId);
            // 文章 votes 字段增加一票
            jedis.hincrBy(articleId, "votes", 1);
        }
        transaction.exec();
    }
}

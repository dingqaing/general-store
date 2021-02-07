package com.me.generalstore.redis.dao.impl;

import com.me.generalstore.redis.consts.RedisDataStructureKey;
import com.me.generalstore.redis.dao.ArticleDao;
import com.me.generalstore.redis.model.article.Article;
import lombok.Setter;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.TimeUnit;

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

    private RedisTemplate<String, Article> redisTemplate;

    private StringRedisTemplate stringRedisTemplate;

    @Override
    public String postArticle(String user, String title, String link) {

        String articleId = redisTemplate.opsForValue().increment("article:") + "";
        String voted = "voted:" + articleId;
        long now = LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8"));
        String article = "article:" + articleId;
        Article articleModel = new Article();
        Map<String, String> map = new HashMap<>(16);
        BeanUtils.copyProperties(articleModel, map);

        stringRedisTemplate.multi();
        stringRedisTemplate
                .opsForSet()
                .add(voted, user);
        stringRedisTemplate.expire(voted, ONE_WEEK_IN_SECOND, TimeUnit.SECONDS);
        stringRedisTemplate.
                <String, String>opsForHash()
                .putAll(article, new HashMap<>(16));
        stringRedisTemplate
                .opsForZSet()
                .add(RedisDataStructureKey.ARTICLE_TIME_ZSET, article, now);
        stringRedisTemplate
                .opsForZSet()
                .add(RedisDataStructureKey.ARTICLE_SCORE_ZSET, article, now + VOTE_SCORE);
        stringRedisTemplate.exec();
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

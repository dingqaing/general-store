package com.me.generalstore.redis.configuration;

import com.me.generalstore.redis.dao.ArticleDao;
import com.me.generalstore.redis.dao.impl.ArticleDaoImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * ArticleVoteHandler 配置类.
 *
 * @author dqq
 * @date 2021 /2/3
 */
@Configuration
public class ArticleVoteHandlerConfiguration {

    @Resource
    private RedisConfiguration redisConfiguration;

    @Bean
    public ArticleDao buildArticleDao() {
        ArticleDaoImpl articleDao = new ArticleDaoImpl();
        articleDao.setJedis(redisConfiguration.buildJedis());
        return articleDao;
    }
}

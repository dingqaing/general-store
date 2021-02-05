package com.me.generalstore.redis.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import redis.clients.jedis.Jedis;

import javax.annotation.Resource;

/**
 * Resdis 配置类.
 *
 * @author dqq
 * @date 2021 /2/3
 */
@Configuration
public class RedisConfiguration {

    @Resource
    private RedisConnectionFactory redisConnectionFactory;

    @Bean
    public Jedis buildJedis() {
        return (Jedis) redisConnectionFactory.getConnection().getNativeConnection();
    }
}

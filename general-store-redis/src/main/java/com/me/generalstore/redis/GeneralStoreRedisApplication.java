package com.me.generalstore.redis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * GeneralStoreRedis Application.
 *
 * @author dqq
 */
@EnableEurekaClient
@SpringBootApplication
public class GeneralStoreRedisApplication {

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(GeneralStoreRedisApplication.class, args);
    }

}

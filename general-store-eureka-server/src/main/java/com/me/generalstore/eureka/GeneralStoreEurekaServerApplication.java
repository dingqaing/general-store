package com.me.generalstore.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * Eureka 注册中心.
 *
 * @author dqq
 * @date 2021-02-05
 */
@EnableEurekaServer
@SpringBootApplication
public class GeneralStoreEurekaServerApplication {

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(GeneralStoreEurekaServerApplication.class, args);
    }
}

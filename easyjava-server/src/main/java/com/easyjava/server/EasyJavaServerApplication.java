package com.easyjava.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * easyjava 可视化控制台服务入口。
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class EasyJavaServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(EasyJavaServerApplication.class, args);
    }
}

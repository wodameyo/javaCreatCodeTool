package com.easyjava;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.easyjava.mappers")
public class RunDemoApplication {
    public static void main(String[] args) {
        org.springframework.boot.SpringApplication.run(RunDemoApplication.class, args);
    }
}

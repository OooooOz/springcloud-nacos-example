package com.example;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
@MapperScan(basePackages = "com.example.*.mapper")
public class MybatisExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(MybatisExampleApplication.class, args);
    }

}

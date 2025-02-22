package com.example;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan(basePackages = "com.example.*.mapper")
@EnableAspectJAutoProxy(exposeProxy = true)
public class MybatisExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(MybatisExampleApplication.class, args);
    }

}

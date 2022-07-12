package com.example.seata;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
//@EnableAutoDataSourceProxy
@MapperScan(basePackages = "com.example.seata.mapper")
public class SeataExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(SeataExampleApplication.class, args);
    }

}

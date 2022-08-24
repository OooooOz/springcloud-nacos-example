package com.example.elasticsearch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class ElasticsearchExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(ElasticsearchExampleApplication.class, args);
    }

}

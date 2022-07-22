package com.example.webscoket;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class WebscoketApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebscoketApplication.class, args);
    }

}

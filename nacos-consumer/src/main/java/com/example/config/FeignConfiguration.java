//package com.example.nacosconsumer.config;
//
//import com.netflix.loadbalancer.IRule;
//import com.netflix.loadbalancer.RandomRule;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class FeignConfiguration {
//    /**
//     * 配置随机的负载均衡策略
//     * 特点：对所有的服务都生效
//     */
//    @Bean
//    public IRule loadBalancedRule() {
//        return new RandomRule();
//    }
//}
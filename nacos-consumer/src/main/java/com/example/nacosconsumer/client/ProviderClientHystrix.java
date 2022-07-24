package com.example.nacosconsumer.client;

import org.springframework.stereotype.Component;

@Component
public class ProviderClientHystrix implements ProviderClient {

    @Override
    public String getValue() {
        return "熔断错误";
    }
}

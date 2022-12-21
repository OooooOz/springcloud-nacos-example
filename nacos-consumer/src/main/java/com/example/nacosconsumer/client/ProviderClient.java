package com.example.nacosconsumer.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(value = "provider")
//@FeignClient(value = "provider", fallback = ProviderClientHystrix.class)
//@FeignClient(name = "provider",url = "http://localhost:8080/provider")
public interface ProviderClient {

    @GetMapping("/provider/getValue")
//    @GetMapping("/getValue")
    String getValue();
}

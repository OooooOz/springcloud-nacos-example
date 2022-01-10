package com.example.nacosconsumer.controller;

import com.example.nacosconsumer.client.ProviderClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

@RequestMapping("/consumer")
@Controller
class ConsumerController {

    @Autowired
    ProviderClient providerClient;

    @GetMapping("/getValue")
    @ResponseBody
    public String getValue(){
        RestTemplate restTemplate = new RestTemplate();
        String forObject = restTemplate.getForObject("http://127.0.0.1:8080/provider/getValue", String.class);
        System.out.println("------------getValue消费者-------------------");
        return forObject;
    }
    @GetMapping("/getFeignValue")
    @ResponseBody
    public String getFeignValue(){
        System.out.println("------------getFeignValue消费者-------------------");
        return providerClient.getValue();
    }
}
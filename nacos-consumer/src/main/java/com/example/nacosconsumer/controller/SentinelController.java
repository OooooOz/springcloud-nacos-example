package com.example.nacosconsumer.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.sun.deploy.security.BlockedException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class SentinelController {

    @GetMapping("/add")
    @SentinelResource(value = "/test/add", blockHandler = "blockHandlerMethod")
    public String add() {
        return "add";
    }

    @GetMapping("/get")
    public String get() {
        return "get";
    }

    public String blockHandlerMethod(BlockedException e) {
        return "流控";
    }
}

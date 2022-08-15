package com.example.nacosconsumer.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/test")
public class SentinelController {

    @GetMapping("/add")
    @SentinelResource(value = "/test/add", blockHandler = "blockHandlerMethod")
    public String add() {
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            System.out.println("==========================");
        }
        return "add";
    }

    @GetMapping("/get")
    public String get() {
        return "get";
    }

    public String blockHandlerMethod(BlockException e) {
        return "流控";
    }
}

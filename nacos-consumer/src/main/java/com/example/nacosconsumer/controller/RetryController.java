package com.example.nacosconsumer.controller;

import com.example.nacosconsumer.service.RetryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/retry")
@RestController
class RetryController {

    @Autowired
    RetryService retryService;

    @GetMapping("/test")
    public String getValue() {
        return retryService.getValue();
    }

}

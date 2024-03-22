package com.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.example.model.BaseResponse;
import com.example.model.dto.ContentDTO;
import com.example.service.ConsumerService;

@RequestMapping("/consumer")
@Controller
class ConsumerController {

    @Autowired
    private ConsumerService consumerService;

    @GetMapping("/getFeignValue")
    @ResponseBody
    public String getFeignValue(){
        System.out.println("------------getFeignValue消费者-------------------");
        return consumerService.getFeignValue();
    }

    @PutMapping("/update")
    public BaseResponse updateOne(@RequestBody ContentDTO dto) {
        consumerService.updateOne(dto);
        return BaseResponse.SUCCESS();
    }
}

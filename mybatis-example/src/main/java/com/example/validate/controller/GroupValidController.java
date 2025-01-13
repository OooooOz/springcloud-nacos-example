package com.example.validate.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.example.model.BaseResponse;
import com.example.validate.dto.GroupSequenceProviderSubmitDTO;
import com.example.validate.dto.GroupSubmitDTO;
import com.example.validate.dto.GroupValid;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/contract")
public class GroupValidController {

    @PostMapping("/person/submit")
    public BaseResponse personSubmit(@RequestBody @Validated(GroupValid.Person.class) GroupSubmitDTO dto) {
        log.info("personSubmit param：{}", JSON.toJSONString(dto));
        return BaseResponse.SUCCESS();
    }

    @PostMapping("/company/submit")
    public BaseResponse companySubmit(@RequestBody @Validated(GroupValid.Company.class) GroupSubmitDTO dto) {
        log.info("companySubmit param：{}", JSON.toJSONString(dto));
        return BaseResponse.SUCCESS();
    }

    @PostMapping("/submit")
    public BaseResponse submit(@RequestBody @Validated GroupSequenceProviderSubmitDTO dto) {
        log.info("companySubmit param：{}", JSON.toJSONString(dto));
        return BaseResponse.SUCCESS();
    }
}

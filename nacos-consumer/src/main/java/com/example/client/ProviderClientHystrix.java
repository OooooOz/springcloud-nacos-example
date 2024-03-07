package com.example.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.example.model.dto.ContentDTO;

import feign.Request;

@Slf4j
@Component
public class ProviderClientHystrix implements ProviderClient {

    @Override
    public String getValue() {
        return "熔断错误";
    }

    @Override
    public JSONObject updateOne(ContentDTO dto, Request.Options options) {
        log.info("接口异常");
        return new JSONObject();
    }

}

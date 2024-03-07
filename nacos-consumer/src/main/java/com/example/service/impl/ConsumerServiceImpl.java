package com.example.service.impl;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.client.ProviderClient;
import com.example.model.dto.ContentDTO;
import com.example.service.ConsumerService;

import feign.Request;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description
 * @Author c-zhongwh01
 * @Date 2024/3/7 17:03
 */
@Slf4j
@Service
public class ConsumerServiceImpl implements ConsumerService {

    @Qualifier("com.example.client.ProviderClient")
    @Autowired
    private ProviderClient providerClient;

    @Override
    public String getFeignValue() {
        return providerClient.getValue();
    }

    @Override
    public void updateOne(ContentDTO dto) {
        JSONObject jsonObject = null;
        log.info("请求开始：{}", JSON.toJSONString(dto));
        try {
            jsonObject = providerClient.updateOne(dto, new Request.Options(3, TimeUnit.SECONDS, 3, TimeUnit.SECONDS, true));
            log.info("响应：{}", jsonObject.toJSONString());
        } catch (Exception e) {
            log.info("异常：{}", e.getMessage());
        }
    }
}

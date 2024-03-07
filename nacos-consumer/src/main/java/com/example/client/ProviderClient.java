package com.example.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.alibaba.fastjson.JSONObject;
import com.example.model.dto.ContentDTO;

import feign.Request;

@FeignClient(value = "provider")
//@FeignClient(value = "provider", fallback = ProviderClientHystrix.class)
public interface ProviderClient {

    @GetMapping("/provider/getValue")
    String getValue();

    /**
     * 超时配置：
     *  https://blog.csdn.net/u011397981/article/details/129127028
     *  https://zhuanlan.zhihu.com/p/650025543
     * @param dto
     * @param options
     * @return
     */
    @PutMapping("/content/update")
    JSONObject updateOne(@RequestBody ContentDTO dto, Request.Options options);
}

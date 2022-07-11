package com.example.seata.feignApi;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("seata-integral")
public interface IntegralClient {

    @PostMapping("/integral/add")
    String addIntegral(@RequestParam("integral") int integral);

    @PostMapping("/integral/ts/add")
    String tsAddIntegral(@RequestParam("integral") int integral);

    @PostMapping("/integral/global/ts/add")
    String globalTsAddIntegral(@RequestParam("integral") int integral);


}

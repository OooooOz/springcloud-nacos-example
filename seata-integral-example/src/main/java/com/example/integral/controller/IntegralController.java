package com.example.integral.controller;

import com.example.integral.mapper.IntegralMapper;
import io.seata.core.context.RootContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IntegralController {

    @Autowired
    private IntegralMapper integralMapper;

    @GetMapping("/integral/add")
    public String addIntegral(@RequestParam("integral") int integral) {
        integralMapper.addIntegral(10L, integral);
        return "已添加积分";
    }

    @PostMapping("/integral/ts/add")
    @Transactional
    public String tsAddIntegral(@RequestParam("integral") int integral) {
        integralMapper.addIntegral(10L, integral);
        int a = 1 / 0;
        return "已添加积分";
    }

    @PostMapping("/integral/global/ts/add")
    public String globalTsAddIntegral(@RequestParam("integral") int integral) {
        System.out.println("integral全局事务id=================>" + RootContext.getXID());
        integralMapper.addIntegral(10L, integral);
        int a = 1 / 0;
        return "已添加积分";
    }
}

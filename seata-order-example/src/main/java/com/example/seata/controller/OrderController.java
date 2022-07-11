package com.example.seata.controller;

import com.example.seata.feignApi.IntegralClient;
import com.example.seata.mapper.OrderMapper;
import io.seata.core.context.RootContext;
import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController

public class OrderController {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private IntegralClient integralClient;

    @PostMapping("/order/add")
    public void addOrder() {
        int order = orderMapper.addOrder("已创建");
    }

    @PostMapping("/order/confirm")
    public String confirmOrder(Long id) {
        orderMapper.confirmOrder(id, "已确认");

        integralClient.addIntegral(20);

        return "已确认";
    }

    @PostMapping("/order/ts/confirm")
    @Transactional
    public String tsConfirmOrder(Long id) {
        orderMapper.confirmOrder(id, "已确认");

        integralClient.tsAddIntegral(20);

        return "已确认";
    }

    @PostMapping("/order/global/ts/confirm")
    @GlobalTransactional(rollbackFor = Exception.class)
    public String globalTsConfirmOrder(Long id) {
        System.out.println("order全局事务id=================>" + RootContext.getXID());
        orderMapper.confirmOrder(id, "已确认");

//        try {
//            integralClient.globalTsAddIntegral(20);
//        } catch (Exception e) {
//            System.out.println("积分服务异常");
//            throw new RuntimeException();
//        }

        integralClient.globalTsAddIntegral(20);

        return "已确认";
    }
}

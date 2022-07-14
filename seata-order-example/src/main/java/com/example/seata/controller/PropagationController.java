package com.example.seata.controller;

import com.example.seata.mapper.OrderMapper;
import com.example.seata.service.PropagationHelper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PropagationController {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private PropagationHelper propagationHelper;

    @PostMapping("/ts/propagation")
    @Transactional(propagation = Propagation.REQUIRED)
    public String testTsPropagation(Long id, String propagation) {

        try {
            if (StringUtils.equals(propagation, "NESTED")) {
                propagationHelper.subTsMethodForNested(id);
            } else {
                propagationHelper.subTsMethodForRequiresNew(id);
            }
        } catch (Exception e) {
        }

        int integral = orderMapper.addIntegral(10L, 20);

        if (integral > 0 && StringUtils.equals(propagation, "NESTED")) {
            throw new RuntimeException("主方法人为异常");
        }
        return "已确认";
    }
}

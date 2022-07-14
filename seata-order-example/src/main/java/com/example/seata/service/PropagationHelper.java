package com.example.seata.service;

import com.example.seata.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PropagationHelper {

    @Autowired
    private OrderMapper orderMapper;

    @Transactional(propagation = Propagation.NESTED)
    public void subTsMethodForNested(Long id) {
        orderMapper.confirmOrder(id, "已确认");
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void subTsMethodForRequiresNew(Long id) {
        orderMapper.confirmOrder(id, "已确认");
        throw new RuntimeException("子方法人为异常");
    }
}

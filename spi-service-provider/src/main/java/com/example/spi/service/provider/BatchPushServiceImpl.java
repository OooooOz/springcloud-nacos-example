package com.example.spi.service.provider;

import com.example.spi.service.PushService;

public class BatchPushServiceImpl implements PushService {

    @Override
    public void push() {
        System.out.println("BatchPushServiceImpl");
    }

    @Override
    public String getName() {
        return "batchPushServiceImpl";
    }
}

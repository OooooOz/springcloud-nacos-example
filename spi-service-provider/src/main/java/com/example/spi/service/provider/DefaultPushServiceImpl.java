package com.example.spi.service.provider;

import com.example.spi.service.PushService;

public class DefaultPushServiceImpl implements PushService {

    @Override
    public void push() {
        System.out.println("DefaultPushServiceImpl");
    }

    @Override
    public String getName() {
        return "defaultPushServiceImpl";
    }
}

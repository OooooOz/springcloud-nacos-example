package com.example.webscoket.controller;

import com.example.webscoket.service.WebSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

/**
 * socket来进行主动将数据发送到前端，
 * 场景：
 * 前端打开组件页面，与后端建立socket长连接通讯，当后端通过其他服务拉取到最新数据，就手动推送到前端
 * 多用于实时展示数据
 */

@Controller
@RequestMapping("/websocket")
public class WebSocketController {

    @Autowired
    private WebSocketService webSocketService;

    @GetMapping("/send")
    @ResponseBody
    public String sendSocketMsg() {

        try {
            webSocketService.sendInfo("服务端返回给web的数据" + System.currentTimeMillis(), "zhangsan");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "发送成功";
    }
}

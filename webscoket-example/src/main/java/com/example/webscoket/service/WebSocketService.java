package com.example.webscoket.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @version V1.0
 * @Title: WebSocket核心类
 * @Package com.ieslab.util.websocket.server
 * @Description: 指定WebSocket服务端所有核心处理
 * @author: zongshaofeng
 * @date 2021/6/26
 */
@Component
@ServerEndpoint(value = "/websocket/{userId}")
@Slf4j
public class WebSocketService {

    /**
     * 用来记录当前的在线连接数，采用JUC的原子类来保证线程安全
     */
    private final static AtomicInteger onlineCount = new AtomicInteger(0);
    /**
     * 创建Map存放每个连接对应的WebSocket对象，采用JUC的ConcurrentHashMap并发Map保证线程安全
     */
    private final static ConcurrentHashMap<String, WebSocketService> webSocketMap = new ConcurrentHashMap<>();
    /**
     * 与某个客户端的连接会话，需要通过它来推送数据
     */
    private Session session;
    /**
     * 接收userId
     */
    private String userId;

    /**
     * 连接建立成功调用的方法
     *
     * @param session
     * @param userId
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId) {
        this.session = session;
        this.userId = userId;
        //记录当前的在线连接数
        int nowOnlineCount = 0;
        if (webSocketMap.containsKey(userId)) {
            webSocketMap.remove(userId);
            //加入到Map中
            webSocketMap.put(userId, this);
        } else {
            webSocketMap.put(userId, this);
            //在线数加1
            nowOnlineCount = onlineCount.incrementAndGet();
        }
        log.info("ID为{}的用户连接成功，当前在线人数为：{}", userId, nowOnlineCount);
        try {
            //发送消息，检验是否连接成功
            sendMessage("来自服务端的提示消息，ID为" + userId + "的用户连接成功！");
        } catch (Exception e) {
            log.error("ID为{}的用户连接失败，失败原因：{}", userId, e.getMessage());
        }
    }

    /**
     * 连接关闭时调用的方法
     */
    @OnClose
    public void onClose() {
        //记录当前的在线连接数
        int nowOnlineCount = 0;
        if (webSocketMap.containsKey(userId)) {
            //移除对应的WebSocket对象，相应的在线连接数减1
            webSocketMap.remove(userId);
            nowOnlineCount = onlineCount.decrementAndGet();
        }
        log.info("ID为{}的用户断开连接，当前在线人数为：{}", userId, nowOnlineCount);
    }

    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        log.info("用户ID：{}，报文：{}", userId, message);
        webSocketMap.get(userId).sendMessage("服务端返回给web的数据" + System.currentTimeMillis());
    }

    /**
     * 发生错误时调用的方法
     *
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("用户错误:{},原因:{}", userId, error.getMessage());
        error.printStackTrace();
    }

    /**
     * 实现服务器主动推送的方法
     *
     * @param message
     * @throws IOException
     */
    public void sendMessage(String message) throws IOException {
        session.getBasicRemote().sendText(message);
    }


    /**
     * 发送自定义消息
     *
     * @param message 消息内容
     * @param userId
     * @throws IOException
     */
    public static void sendInfo(String message, @PathParam("userId") String userId) throws IOException {
        log.info("发送消息到:{}，报文:{}", userId, message);
        if (StringUtils.isNotBlank(userId) && webSocketMap.containsKey(userId)) {
            webSocketMap.get(userId).sendMessage(message);
        } else {
            log.error("用户{},不在线！", userId);
        }
    }

}

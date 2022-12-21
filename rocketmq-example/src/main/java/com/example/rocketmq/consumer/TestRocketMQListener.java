package com.example.rocketmq.consumer;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Slf4j
@Service
//@RocketMQMessageListener(consumerGroup = "rocketmq-test-consumer-group", topic = "rocketmq-test-topic", selectorExpression = "tagA")
@RocketMQMessageListener(consumerGroup = "rocketmq-test-consumer-group", topic = "${rocketmq.producer.topic}", selectorExpression = "*")
public class TestRocketMQListener implements RocketMQListener<MessageExt> {

    @Override
    public void onMessage(MessageExt msg) {
        log.info("收到一个消息", JSON.toJSONString(msg));
        try {
            String message = new String(msg.getBody(), RemotingHelper.DEFAULT_CHARSET);
            log.info("message: " + message);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}

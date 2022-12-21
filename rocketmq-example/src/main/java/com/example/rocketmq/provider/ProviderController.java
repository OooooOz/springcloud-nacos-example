package com.example.rocketmq.provider;

import com.alibaba.fastjson.JSON;
import com.example.rocketmq.vo.UserInfoVO;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rocketmq")
public class ProviderController {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Value("${rocketmq.producer.topic}")
    private String topic;

    @Value("${rocketmq.producer.group}")
    private String group;


    /**
     * 同步消息：生产者消息投递到队列的过程是同步的，需要等队列中的消息发送到broke才进行消息投递
     */
    @GetMapping("/sync/msg")
    public void syncSend() {
        String json = "同步消息";
        SendResult sendMessage = rocketMQTemplate.syncSend(topic, json);
        System.out.println(sendMessage);
    }

    @GetMapping("/normal/msg")
    public void sendNormalMsg() {
        // convertAndSend是父类方法，最终还是同步发送
        rocketMQTemplate.convertAndSend(topic, "普通字符串消息");
        rocketMQTemplate.convertAndSend(topic, UserInfoVO.builder().userId(1L).name("name").build());
    }

    @GetMapping("/tagA/msg")
    public void sendNormalMsgWithTag() {
        UserInfoVO userInfoVO = UserInfoVO.builder().userId(1L).name("name").age("18").build();
        rocketMQTemplate.convertAndSend(topic + ":tagA", JSON.toJSON(userInfoVO));
        rocketMQTemplate.convertAndSend(topic + ":tagB", "TagB普通字符串消息");
    }

    /**
     * 异步消息：生产者消息投递到队列的过程是异步的（通过新的线程进行发送），不等队列中的消息发送到broke就进行消息投递
     */
    @GetMapping("/async/msg")
    public void asyncSend() {
        String json = "异步消息";
        SendCallback callback = new SendCallback() {
            @Override
            public void onSuccess(SendResult sendResult) {
                System.out.println("123");
            }

            @Override
            public void onException(Throwable throwable) {
                System.out.println("456");
            }
        };
        rocketMQTemplate.asyncSend("sendMessage", json, callback);
        System.out.println("ProviderController.asyncSend");
    }

    /**
     * 单向消息
     */
    @GetMapping("/oneWay/msg")
    public void sendOneWay() {
        String json = "单向消息";
        rocketMQTemplate.sendOneWay("sendMessage", json);
    }

    /**
     * 单向消息
     */
    @GetMapping("/transaction/msg")
    public void sendTransaction() {
        String json = "事务消息";
        Message<String> msg = MessageBuilder.withPayload(json).build();
        // 第一个参数必须与@RocketMQTransactionListener的成员字段'transName'相同
        TransactionSendResult sendResult = rocketMQTemplate.sendMessageInTransaction("rocketmq-test-provider-tx-group", topic, msg, null);
        System.out.println(sendResult);
    }

    /**
     * 使用注解@RocketMQTransactionListener定义事务监听器
     * txProducerGroup要保持一致，切不能被别的生产者组占用
     */
    @RocketMQTransactionListener(txProducerGroup = "rocketmq-test-provider-tx-group")
    class TransactionListenerImpl implements RocketMQLocalTransactionListener {
        @Override
        public RocketMQLocalTransactionState executeLocalTransaction(Message msg, Object arg) {
            System.out.println("TransactionListenerImpl.executeLocalTransaction");
            // 执行本地事务，成功返回COMMIT，如果返回UNKNOWN；服务端会有定时任务进行服务状态回查
            return RocketMQLocalTransactionState.UNKNOWN;
        }

        /**
         * 服务状态回查
         *
         * @param msg
         * @return
         */
        @Override
        public RocketMQLocalTransactionState checkLocalTransaction(Message msg) {
            // 返回COMMIT，则消息能够给消费者去消费
            // 返回UNKNOWN，继续回查，知道15次，之后便ROLLBACK回滚
            return RocketMQLocalTransactionState.COMMIT;
        }
    }
}

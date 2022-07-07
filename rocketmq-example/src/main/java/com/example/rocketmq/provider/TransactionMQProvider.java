package com.example.rocketmq.provider;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.*;

public class TransactionMQProvider {
    public static void main(String[] args) throws MQClientException, UnsupportedEncodingException {
        //创建生产者并制定组名
        TransactionMQProducer producer = new TransactionMQProducer("rocketMQ_transaction_producer_group");
        //2.指定Nameserver地址
        producer.setNamesrvAddr("192.168.182.128:9876");
        //3、指定消息监听对象用于执行本地事务和消息回查
        TransactionListener listener = new TransactionListenerImpl();
        producer.setTransactionListener(listener);
        //4、线程池
        ExecutorService executorService = new ThreadPoolExecutor(2, 5, 100, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(2000), new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = newThread(r);
                thread.setName("client-transaction-msg-check-thread");
                return thread;
            }
        });
        producer.setExecutorService(executorService);
        //5、启动producer
        producer.start();

        //6.创建消息对象，指定主题Topic、Tag和消息体 String topic, String tags, String keys, byte[] body
        Message message = new Message("Topic_transaction_demo", //主题
                "Tags", //主要用于消息过滤
                "Key_1", //消息唯一值
                ("hello-transaction").getBytes(RemotingHelper.DEFAULT_CHARSET));

        //7、发送事务消息
        TransactionSendResult result = producer.sendMessageInTransaction(message, "hello-transaction");

        producer.shutdown();
    }
}

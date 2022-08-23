package com.example.rocketmq.provider;

import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.concurrent.ConcurrentHashMap;


//@RocketMQTransactionListener
public class TransactionListenerImpl implements TransactionListener {
    //存储事务状态信息  key:事务id  value：当前事务执行的状态
    private ConcurrentHashMap<String, Integer> localTrans = new ConcurrentHashMap<>();

    //执行本地事务
    @Override
    public LocalTransactionState executeLocalTransaction(Message message, Object o) {
        //事务id
        String transactionId = message.getTransactionId();
        //0:执行中，状态未知 1：执行成功 2：执行失败
        localTrans.put(transactionId, 0);
        //业务执行，本地事务，service
        System.out.println("hello-demo-transaction");
        try {
            System.out.println("正在执行本地事务---");
            Thread.sleep(60000 * 2);
            System.out.println("本地事务执行成功---");
            localTrans.put(transactionId, 1);
        } catch (InterruptedException e) {
            e.printStackTrace();
            localTrans.put(transactionId, 2);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return LocalTransactionState.ROLLBACK_MESSAGE;
        }
        return LocalTransactionState.COMMIT_MESSAGE;
    }

    //消息回查
    @Override
    public LocalTransactionState checkLocalTransaction(MessageExt messageExt) {
        //获取对应事务的状态信息
        String transactionId = messageExt.getTransactionId();
        //获取对应事务id执行状态
        Integer status = localTrans.get(transactionId);
        //消息回查
        System.out.println("消息回查---transactionId:" + transactionId + "状态:" + status);
        switch (status) {
            case 0:
                return LocalTransactionState.UNKNOW;
            case 1:
                return LocalTransactionState.COMMIT_MESSAGE;
            case 2:
                return LocalTransactionState.ROLLBACK_MESSAGE;
        }
        return LocalTransactionState.UNKNOW;
    }
}
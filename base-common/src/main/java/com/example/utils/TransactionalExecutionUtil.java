package com.example.utils;

import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TransactionalExecutionUtil {

    private TransactionalExecutionUtil() {}

    /**
     * 执行一个操作，该操作将在当前事务提交后执行。如果没有活跃的事务，立即执行该操作。
     *
     * @param action 要执行的业务操作
     */
    public static void executeAfterTransactionCommit(Runnable action) {
        if (!TransactionSynchronizationManager.isActualTransactionActive()) {
            log.info("[TransactionalExecutionUtil#executeAfterTransactionCommit]当前线程不存在活跃事务，直接直接执行");
            // 如果没有活跃的事务，立即执行操作
            action.run();
            return;
        }

        // 如果有活跃的事务，注册一个同步器，在事务提交后执行操作
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                action.run();
            }
        });
    }
}

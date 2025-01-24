package com.example.common.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.example.common.model.dto.CommonEventDTO;
import com.example.common.model.dto.NotifySystemDTO;
import com.example.common.service.CommonConfigService;
import com.example.utils.TransactionalExecutionUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * 通用事件监听器
 */
@Slf4j
@Component
public class CommonHandleListener {

    public static final String NOTIFY_OTHER_SYSTEM_EVENT = "onNotifyOtherSystemEvent";

    @Autowired
    private CommonConfigService commonConfigService;

    @EventListener(condition = "#eventDTO.source == T(com.example.common.listener.CommonHandleListener).NOTIFY_OTHER_SYSTEM_EVENT")
    public void onNotifyOtherSystemEvent(CommonEventDTO eventDTO) {
        log.info("EventListener事务提交后执行");
        if (NOTIFY_OTHER_SYSTEM_EVENT.equals(eventDTO.getSource())) {
            TransactionalExecutionUtil.executeAfterTransactionCommit(() -> {
                // afterCommit方法如果发生异常,不会影响之前事务的提交
                // throw new RuntimeException("afterCommit方法如果发生异常");

                if (eventDTO.getTargetClass() instanceof NotifySystemDTO) {
                    NotifySystemDTO dto = (NotifySystemDTO)eventDTO.getTargetClass();
                    commonConfigService.doNotifyOtherSystem(dto);
                }
            });
        }
    }

    @TransactionalEventListener(
        // 监听的阶段
        phase = TransactionPhase.AFTER_COMMIT,
        // 监听的条件
        condition = "#eventDTO.source == T(com.example.common.listener.CommonHandleListener).NOTIFY_OTHER_SYSTEM_EVENT")
    public void onNotifyOtherSystemTransactionalEvent(CommonEventDTO eventDTO) {
        log.info("TransactionalEventListener事务提交后执行");
        if (NOTIFY_OTHER_SYSTEM_EVENT.equals(eventDTO.getSource())) {
            if (eventDTO.getTargetClass() instanceof NotifySystemDTO) {
                NotifySystemDTO dto = (NotifySystemDTO)eventDTO.getTargetClass();
                commonConfigService.doNotifyOtherSystem(dto);
            }
        }
    }
}

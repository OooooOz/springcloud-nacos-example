package com.example.common.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.mapper.CommonConfigMapper;
import com.example.common.model.dto.CommonConfigDTO;
import com.example.common.model.entity.CommonConfig;
import com.example.common.service.CommonConfigService;
import com.example.utils.TransactionalExecutionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class CommonConfigServiceImpl extends ServiceImpl<CommonConfigMapper, CommonConfig> implements CommonConfigService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long submit(CommonConfigDTO commonConfigDTO) {
        TransactionalExecutionUtil.executeAfterTransactionCommit(() -> {
            log.info("事务提交后执行：{}", JSON.toJSONString(commonConfigDTO));
            // afterCommit方法如果发生异常,不会影响之前事务的提交
            // throw new RuntimeException("afterCommit方法如果发生异常");
            CommonConfigService self = (CommonConfigService) AopContext.currentProxy();
            self.otherTransactionalOperate(commonConfigDTO);
        });
        Long id = this.doSomething(commonConfigDTO);
        commonConfigDTO.setId(id);
        return id;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void otherTransactionalOperate(CommonConfigDTO commonConfigDTO) {
        log.info("otherTransactionalOperate, id：{}", commonConfigDTO.getId());
        CommonConfig update = new CommonConfig();
        update.setId(commonConfigDTO.getId());
        update.setConfigValue("Update");
        this.updateById(update);
    }

    private Long doSomething(CommonConfigDTO commonConfigDTO) {
        log.info("事务提交前执行doSomething：{}", JSON.toJSONString(commonConfigDTO));
        CommonConfig entity = BeanUtil.copyProperties(commonConfigDTO, CommonConfig.class);
        if (ObjectUtil.isNull(commonConfigDTO.getId())) {
            this.save(entity);
        } else {
            this.updateById(entity);
        }
        return entity.getId();
    }
}

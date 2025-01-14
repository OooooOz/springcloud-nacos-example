package com.example.common.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.mapper.CommonConfigMapper;
import com.example.common.model.dto.CommonConfigDTO;
import com.example.common.model.entity.CommonConfig;
import com.example.common.service.CommonConfigService;
import com.example.utils.TransactionalExecutionUtil;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CommonConfigServiceImpl extends ServiceImpl<CommonConfigMapper, CommonConfig> implements CommonConfigService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long submit(CommonConfigDTO commonConfigDTO) {
        TransactionalExecutionUtil.executeAfterTransactionCommit(() -> {
            log.info("事务提交后执行：{}", JSON.toJSONString(commonConfigDTO));
        });
        CommonConfig entity = BeanUtil.copyProperties(commonConfigDTO, CommonConfig.class);
        if (ObjectUtil.isNull(commonConfigDTO.getId())) {
            this.save(entity);
        } else {
            this.updateById(entity);
        }
        log.info("事务提交前执行：{}", JSON.toJSONString(entity));
        return entity.getId();
    }
}

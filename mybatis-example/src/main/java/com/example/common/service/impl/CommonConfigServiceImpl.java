package com.example.common.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.mapper.CommonConfigMapper;
import com.example.common.model.dto.CommonConfigDTO;
import com.example.common.model.dto.NotifySystemDTO;
import com.example.common.model.entity.CommonConfig;
import com.example.common.model.vo.DetailVo;
import com.example.common.service.CommonConfigService;
import com.example.utils.TransactionalExecutionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class CommonConfigServiceImpl extends ServiceImpl<CommonConfigMapper, CommonConfig> implements CommonConfigService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long submit(CommonConfigDTO commonConfigDTO) {
        Long id = this.dealMainInfo(commonConfigDTO);
        List<DetailVo> detailVos = this.dealMainDetailInfo(id);

        TransactionalExecutionUtil.executeAfterTransactionCommit(() -> {
            log.info("事务提交后执行");
            // afterCommit方法如果发生异常,不会影响之前事务的提交
            // throw new RuntimeException("afterCommit方法如果发生异常");
            NotifySystemDTO dto = this.buildNotifySystemDTO(id, detailVos);
            this.doNotifyOtherSystem(dto);
        });
        log.info("事务提交前操作：{}", JSON.toJSONString(commonConfigDTO));
        return id;
    }

    private List<DetailVo> dealMainDetailInfo(Long id) {
        return this.findById(id);
    }

    private List<DetailVo> findById(Long id) {
        log.info("findById, id：{}", id);
        return Collections.emptyList();
    }

    @Override
    public void notifyOtherSystem(Long id) {
        // 根据id查明细
        List<DetailVo> detailVos = this.findById(id);
        // 构建参数，推送其他系统
        NotifySystemDTO dto = this.buildNotifySystemDTO(id, detailVos);
        this.doNotifyOtherSystem(dto);
    }

    private void doNotifyOtherSystem(NotifySystemDTO dto) {
        log.info("doNotifyOtherSystem, param：{}", JSON.toJSONString(dto));
    }

    private NotifySystemDTO buildNotifySystemDTO(Long id, List<DetailVo> detailVos) {
        NotifySystemDTO notifySystemDTO = new NotifySystemDTO();
        notifySystemDTO.setId(id);
        notifySystemDTO.setDetailVos(detailVos);
        return notifySystemDTO;
    }

    private Long dealMainInfo(CommonConfigDTO commonConfigDTO) {
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

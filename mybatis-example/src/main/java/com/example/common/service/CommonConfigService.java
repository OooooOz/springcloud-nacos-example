package com.example.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.common.model.dto.CommonConfigDTO;
import com.example.common.model.dto.NotifySystemDTO;
import com.example.common.model.entity.CommonConfig;

public interface CommonConfigService extends IService<CommonConfig> {

    /**
     * 提交配置
     *
     * @param commonConfigDTO
     * @return
     */
    NotifySystemDTO submit(CommonConfigDTO commonConfigDTO);

    void doNotifyOtherSystem(NotifySystemDTO dto);
}

package com.example.importing.model.vo;

import cn.hutool.extra.spring.SpringUtil;
import com.commons.exporting.infrastructure.handle.ExcelDynamicSelect;
import com.example.common.model.entity.CommonConfig;
import com.example.common.service.CommonConfigService;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * @Description
 * @Author c-zhongwh01
 * @Date 2025/4/7 14:04
 */
public class ConfigSelectVo implements ExcelDynamicSelect {
    @Override
    public String[] getSource() {
        CommonConfigService configService = SpringUtil.getBean(CommonConfigService.class);
        List<CommonConfig> list = configService.list();
        if (CollectionUtils.isNotEmpty(list)) {
            return list.stream().map(CommonConfig::getConfigValue).toArray(String[]::new);
        }
        return new String[0];
    }
}

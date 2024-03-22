package com.example.service.strategy;

import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.utils.CommonUtil;

/**
 * 策略工厂类
 */
@Component
public class SliceUploadFactory {

    @Autowired
    private Map<String, SliceUploadStrategy> map;

    public SliceUploadStrategy getStrategyByMode(String mode) {
        CommonUtil.checkBusinessException(MapUtils.isNotEmpty(map), "没有处理的策略类");
        SliceUploadStrategy strategy = map.get(mode);
        CommonUtil.checkBusinessException(strategy != null, "没有处理的策略类");
        return strategy;
    }

}

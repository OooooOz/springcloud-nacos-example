package com.example.log.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.log.mapper.KeyLogMapper;
import com.example.log.model.bo.KeyLogBo;
import com.example.log.model.po.KeyLog;
import com.example.log.service.KeyLogService;
import com.example.utils.ExceptionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
* @author Mr.zhong
* @description 针对表【t_key_log】的数据库操作Service实现
* @createDate 2025-02-22 22:51:17
*/
@Slf4j
@Service
public class KeyLogServiceImpl extends ServiceImpl<KeyLogMapper, KeyLog> implements KeyLogService{

    @Override
    public void saveLog(KeyLogBo keyLogBo, String appendLog) {
        try {
            if (keyLogBo == null) {
                return;
            }

            if (StringUtils.isNotBlank(keyLogBo.getRepeatKey())) {
                KeyLog exist = lambdaQuery().eq(KeyLog::getModule, keyLogBo.getModule()).eq(KeyLog::getFunc, keyLogBo.getFunc()).eq(KeyLog::getRepeatKey, keyLogBo.getRepeatKey()).last("limit 1").one();
                if (exist != null) {
                    return;
                }
            }
            KeyLog entity = BeanUtil.copyProperties(keyLogBo, KeyLog.class);
            Object param = keyLogBo.getParam();
            if (param != null) {
                if (param  instanceof String) {
                    entity.setParam((String) param);
                } else {
                    entity.setParam(JSON.toJSONString(param));
                }
            }
            entity.setLogInfo(appendLog);
            this.save(entity);
        } catch (Exception e) {
            log.info("saveLoge exception：{}", ExceptionUtil.printFullStackTraceAndIgnoreLineFeed(e));
        }
    }
}





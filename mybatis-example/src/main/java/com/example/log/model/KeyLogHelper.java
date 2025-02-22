package com.example.log.model;

import cn.hutool.extra.spring.SpringUtil;
import com.example.log.model.bo.KeyLogBo;
import com.example.log.service.KeyLogService;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.util.Assert;

@Data
public class KeyLogHelper {

    private static final KeyLogService keyLogService = SpringUtil.getBean(KeyLogService.class);
    private static Logger logger = LoggerFactory.getLogger(KeyLogHelper.class);

    public static boolean log(String appendLogPattern, Object... appendLogArguments) {
        KeyLogBo keyLogBo = new KeyLogBo(false);
        return log(keyLogBo, appendLogPattern, appendLogArguments);
    }


    public static boolean dbLog(Object param, String appendLogPattern, Object... appendLogArguments) {
        KeyLogBo keyLogBo = new KeyLogBo(true);
        keyLogBo.setParam(param);
        return log(keyLogBo, appendLogPattern, appendLogArguments);
    }

    public static boolean log(KeyLogBo keyLogBo, String appendLogPattern, Object... appendLogArguments) {
        Assert.notNull(keyLogBo, "keyLogBo must not be null");
        FormattingTuple ft = MessageFormatter.arrayFormat(appendLogPattern, appendLogArguments);
        String appendLog = ft.getMessage();
        return logDetail(keyLogBo, appendLog);
    }



    private static boolean logDetail(KeyLogBo keyLogBo, String appendLog) {
        StackTraceElement callInfo = (new Throwable()).getStackTrace()[1];
        if (StringUtils.isBlank(keyLogBo.getModule())) {
            keyLogBo.setModule(callInfo.getClassName());
        }
        if (StringUtils.isBlank(keyLogBo.getFunc())) {
            keyLogBo.setFunc(callInfo.getMethodName());
        }
        // 日志格式化
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(" ").append("[" + keyLogBo.getModule() + "#" + keyLogBo.getFunc() + "]");
        stringBuffer.append(" ").append(appendLog != null ? appendLog : "");
        String formatAppendLog = stringBuffer.toString();
        logger.info(formatAppendLog);
        if (keyLogBo.isSaveDb()) {
            keyLogService.saveLog(keyLogBo, appendLog);
        }
        return true;
    }
}

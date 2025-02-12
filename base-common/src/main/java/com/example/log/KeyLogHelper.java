package com.example.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;
import org.springframework.util.Assert;

public class KeyLogHelper {

    private static Logger logger = LoggerFactory.getLogger(KeyLogHelper.class);

    public static boolean log(String appendLogPattern, Object... appendLogArguments) {
        StackTraceElement callInfo = (new Throwable()).getStackTrace()[1];
        KeyLogBo keyLogBo = new KeyLogBo(callInfo.getClassName(), callInfo.getMethodName(), false);
        return log(keyLogBo, appendLogPattern, appendLogArguments);
    }

    public static boolean log(KeyLogBo keyLogBo ,String appendLogPattern, Object... appendLogArguments) {
        Assert.notNull(keyLogBo, "keyLogBo must not be null");
        FormattingTuple ft = MessageFormatter.arrayFormat(appendLogPattern, appendLogArguments);
        String appendLog = ft.getMessage();
        return logDetail(keyLogBo, appendLog);
    }



    private static boolean logDetail(KeyLogBo keyLogBo, String appendLog) {
        // 日志格式化
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(" ").append("[" + keyLogBo.getModule() + "#" + keyLogBo.getFunc() + "]");
        stringBuffer.append(" ").append(appendLog != null ? appendLog : "");
        String formatAppendLog = stringBuffer.toString();
        if (!keyLogBo.isSaveDb()) {
            logger.info(formatAppendLog);
        } else {

        }
        return true;
    }
}

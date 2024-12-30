package com.example.utils;

import org.apache.commons.lang3.exception.ExceptionUtils;

public class ExceptionUtil {

    private ExceptionUtil() {}

    /**
     * 异常栈完整日志抛出，去掉换行符回车符
     *
     * @param e
     * @return
     */
    public static String printFullStackTraceAndIgnoreLineFeed(Exception e) {
        return ExceptionUtils.getStackTrace(e).replaceAll("[\\n\\r]", "");
    }

    public static String printFullStackTraceAndIgnoreLineFeed(Throwable throwable) {
        return ExceptionUtils.getStackTrace(throwable).replaceAll("[\\n\\r]", "");
    }
}

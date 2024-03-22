package com.example.utils;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;

import com.example.model.BusinessException;

public class CommonUtil {

    private CommonUtil() {}

    public static void checkBusinessException(boolean condition, String msg) {
        if (!condition) {
            throw BusinessException.failMsg(msg);
        }
    }

    public static <E extends Enum<E>> E checkOperateType(Class<E> enumClass, String type, String title) {
        checkBusinessException(StringUtils.isNotBlank(type), title + "不能为空");
        E anEnum = EnumUtils.getEnum(enumClass, type);
        checkBusinessException(anEnum != null, title + "不正确");
        return anEnum;
    }
}

package com.example.model;

import lombok.Getter;


@Getter
public enum ResponseEnum {
    //系统常用异常
    UNKNOWN_ERROR("5000", "未知错误"),
    FAILURE("-1", "失败"),
    SUCCESS("0", "成功");
    //各业务异常

    ResponseEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    private final String code;
    private final String msg;


}

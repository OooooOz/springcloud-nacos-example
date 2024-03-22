package com.example.model;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 返回基类
 */
@Getter
@Setter
@ToString
public class BaseResponse<T> implements Serializable {
    private static final long serialVersionUID = 3230910710976511975L;
    /**
     * 定义的code
     */
    private String code = ResponseEnum.SUCCESS.getCode();
    /**
     * 消息
     */
    private String message = ResponseEnum.SUCCESS.getMsg();

    /**
     * 是否成功
     */
    private boolean isSuccess = true;

    /**
     * 链路追踪id
     */
    private String traceId;

    /**
     * 结果
     */
    private T data;

    public BaseResponse() {
    }

    public static <T> BaseResponse<T> UNKNOWN() {
        BaseResponse<T> resp = new BaseResponse<>();
        resp.setMessage(ResponseEnum.UNKNOWN_ERROR.getMsg());
        resp.setCode(ResponseEnum.UNKNOWN_ERROR.getCode());
        resp.setSuccess(false);
        return resp;
    }

    public static <T> BaseResponse<T> SUCCESS() {
        BaseResponse<T> resp = new BaseResponse<>();
        resp.setMessage(ResponseEnum.SUCCESS.getMsg());
        resp.setCode(ResponseEnum.SUCCESS.getCode());
        return resp;
    }

    public static <T> BaseResponse<T> SUCCESS(T t) {
        BaseResponse<T> resp = new BaseResponse<>();
        resp.setMessage(ResponseEnum.SUCCESS.getMsg());
        resp.setCode(ResponseEnum.SUCCESS.getCode());
        resp.setData(t);
        return resp;
    }

    public static <T> BaseResponse<T> FAILURE(ResponseEnum responseEnum) {
        BaseResponse<T> resp = new BaseResponse<>();
        resp.setMessage(responseEnum.getMsg());
        resp.setCode(responseEnum.getCode());
        resp.setSuccess(false);
        return resp;
    }

    public static <T> BaseResponse<T> FAILURE(String msg) {
        BaseResponse<T> resp = new BaseResponse<>();
        resp.setMessage(msg);
        resp.setCode(ResponseEnum.FAILURE.getCode());
        resp.setSuccess(false);
        return resp;
    }
}

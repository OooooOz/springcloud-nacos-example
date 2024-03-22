package com.example.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/***
 * 系统业务异常
 *
 */
@Data
@NoArgsConstructor
@ToString
public class BusinessException extends RuntimeException {

    private String errCode;

    private String errMsg;

    public BusinessException(String code, String errMsg) {
        super(errMsg);
        this.errCode = code;
        this.errMsg = errMsg;
    }

    public static BusinessException failMsg(String errMsg) {
        return new BusinessException(ResponseEnum.FAILURE.getCode(), errMsg);
    }

    public static BusinessException fail(String code, String errMsg) {
        return new BusinessException(code, errMsg);
    }
}

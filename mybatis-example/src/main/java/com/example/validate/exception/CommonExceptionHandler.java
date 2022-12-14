package com.example.validate.exception;

import com.example.response.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;

@RestControllerAdvice
public class CommonExceptionHandler {

    /**
     * 入参为实体类，参数校验失败抛出的异常MethodArgumentNotValidException拦截
     *
     * @param ex
     * @return
     */
    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public BaseResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        StringBuilder sb = new StringBuilder("校验失败:");
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            sb.append(fieldError.getField()).append("：").append(fieldError.getDefaultMessage()).append(", ");
        }
        String msg = sb.toString();
        return BaseResponse.FAILURE(msg);
    }

    /**
     * 入参不是实体类，参数校验失败抛出的异常ConstraintViolationException拦截
     *
     * @param ex
     * @return
     */
    @ExceptionHandler({ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public BaseResponse handleConstraintViolationException(ConstraintViolationException ex) {
        return BaseResponse.FAILURE(ex.getMessage());
    }

    /**
     * 入参为实体类集合，参数校验失败抛出的异常NotReadablePropertyException拦截,但无法获取哪个字段参数校验失败
     *
     * @param ex
     * @return
     */
//    @ExceptionHandler({NotReadablePropertyException.class})
//    @ResponseStatus(HttpStatus.OK)
//    @ResponseBody
//    public BaseResponse handleNotReadablePropertyException(NotReadablePropertyException ex) {
//        return BaseResponse.FAILURE(ex.getMessage());
//    }
}

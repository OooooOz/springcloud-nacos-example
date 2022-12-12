package com.example.log;

import java.lang.annotation.*;


@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperateLog {

    /**
     * 服务id
     */
    String serviceId() default "";


    /**
     * 用户名
     */
    String userName() default "";

    /**
     * 操作类型
     */
    String operationType() default "";

}

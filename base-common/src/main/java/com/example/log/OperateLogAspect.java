package com.example.log;

import cn.hutool.core.io.IoUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * Author: hsw
 * Date: 2022/04/22/9:30
 */
@Aspect
@Component
@Log4j2
public class OperateLogAspect {

    @Pointcut(value = "@annotation(com.example.log.OperateLog)")
    public void cutService() {
    }

    //使用环绕通知，获取接口请求和返回参数
    @Around("cutService()")
    public void logAroundController(ProceedingJoinPoint pjp) {

        //获取当前请求对象
        //这个RequestContextHolder是Springmvc提供来获得请求的东西
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();

        //请求入参
        String postData = "";
        try {
            postData = IoUtil.read(request.getInputStream(), Charset.forName("UTF-8"));
        } catch (IOException e) {
            log.error("##error found", e);
        }
        System.out.println("postData = " + postData);
        //获取进入的类名
        String className = pjp.getSignature().getDeclaringTypeName();
        System.out.println("className = " + className);
        // 获取方法签名
        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        // 调用方法名称
        String methodName = pjp.getSignature().getName();
        System.out.println("methodName = " + methodName);

        //获取方法上的注解
        OperateLog reqLog = methodSignature.getMethod().getAnnotation(OperateLog.class);
        System.out.println("reqLog = " + reqLog);
        //请求方法中文名称
        String userName = SpelUtil.generateKeyBySpEL(reqLog.userName(), pjp);
        System.out.println("userName = " + userName);
        // 请求的方法参数值
        Object[] args = pjp.getArgs();
        System.out.println("args = " + JSONObject.toJSONString(args));

        //获取返回对象

        try {
            // 接口调用
            Object o = pjp.proceed();
            System.out.println(o);
        } catch (Throwable e) {
            e.printStackTrace();
        }

    }
}

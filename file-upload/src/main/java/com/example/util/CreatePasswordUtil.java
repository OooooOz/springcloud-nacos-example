package com.example.util;

import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CreatePasswordUtil {

    @Autowired
    private StringEncryptor stringEncryptor;

    public void encrypt() {
        log.info("-----生成加密串-----");
        log.info(stringEncryptor.encrypt("nK0VbeOjIWWS1k!cIasp#(Uq0mWQw6S%"));
    }

    public void decrypt() {
        log.info("-----生成解密串-----");
        log.info(stringEncryptor.decrypt("8FcMc3huKdBJcvbfVsqjJwu3D23Iz4AVZbrxigymMOyuGJ8KVYRD8nA2MY2PV0Av40IsU/Md8P6lekme6ORNtUJ2UJ/PC1AuRW9+xZxxGz0="));
    }
}

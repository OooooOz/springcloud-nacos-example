package com.example.util;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

/**
 * 计算文件MD5工具类
 */
public class FileMD5Util {

    private final static Logger logger = LoggerFactory.getLogger(FileMD5Util.class);

    public static String getFileMD5(MultipartFile file) {
        try {
            byte[] uploadBytes = file.getBytes();
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] digest = md5.digest(uploadBytes);
            String hashString = new BigInteger(1, digest).toString(16);
            return hashString;
        } catch (IOException e) {
            logger.error("get file md5 error!!!", e);
        } catch (NoSuchAlgorithmException e) {
            logger.error("get file md5 error!!!", e);
        }
        return null;
    }
}

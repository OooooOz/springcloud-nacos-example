package com.example.service.helper;

import java.math.BigInteger;

import javax.annotation.Resource;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class IdGenerateHandler {
    public static final String BLANK = " ";
    public static final String ERROR_MSG = "编码前缀为空";
    public static final String SUFFIX_FORMAT_LENGTH_10 = "%010d";
    public static final String SUFFIX_FORMAT_LENGTH_8 = "%08d";
    public static final String SUFFIX_FORMAT_LENGTH_8_STR = "%8s";
    static final String NEXT_CODE_KEY = "@NEXT_CODE_METEORITE";
    static final String NEXT_ID_PREFIX = "@NEXT_ID_";
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    public String nextId4Eight(String prefix) {
        Assert.notNull(prefix, ERROR_MSG);
        String hashKey = NEXT_ID_PREFIX + prefix;
        long uid = this.stringRedisTemplate.opsForHash().increment(NEXT_CODE_KEY, hashKey, 1L);
        return prefix.toUpperCase() + String.format(SUFFIX_FORMAT_LENGTH_8, uid);
    }

    public String nextId4Ten(String prefix) {
        Assert.notNull(prefix, ERROR_MSG);
        String hashKey = NEXT_ID_PREFIX + prefix;
        long uid = this.stringRedisTemplate.opsForHash().increment(NEXT_CODE_KEY, hashKey, 1L);
        return prefix.toUpperCase() + String.format(SUFFIX_FORMAT_LENGTH_10, uid);
    }

    /**
     * 前缀+8位字符串（10进制转36进制，字符串长度不足8位前面补0）
     *
     * @param prefix
     * @return
     */
    public String generateByPrefix(String prefix) {
        Assert.notNull(prefix, ERROR_MSG);
        String hashKey = NEXT_ID_PREFIX + prefix;
        long uid = this.stringRedisTemplate.opsForHash().increment(NEXT_CODE_KEY, hashKey, 1L);
        //
        String base36 = new BigInteger(String.valueOf(uid)).toString(36);

        String paddedResult = String.format(SUFFIX_FORMAT_LENGTH_8_STR, base36).replace(BLANK, "0");

        return prefix.toUpperCase() + paddedResult.toUpperCase();
    }

}

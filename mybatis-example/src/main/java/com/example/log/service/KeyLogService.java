package com.example.log.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.log.model.bo.KeyLogBo;
import com.example.log.model.po.KeyLog;

/**
* @author Mr.zhong
* @description 针对表【t_key_log】的数据库操作Service
* @createDate 2025-02-22 22:51:17
*/
public interface KeyLogService extends IService<KeyLog> {

    void saveLog(KeyLogBo keyLogBo, String appendLog);
}

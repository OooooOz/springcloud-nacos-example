package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.model.po.UserAccount;

/**
 * @author c-zhongwh01
 * @description 针对表【t_user_account(用户账户表)】的数据库操作Service
 * @createDate 2024-03-11 22:25:55
 */
public interface UserAccountService extends IService<UserAccount> {

    void saveUserAccount(Integer count);

    void deadLock();

    void mockOtherTransactional();

    void deadLockSolutionOne();

    void mockOtherTransactionalSolutionOne();

    void timeOut(Integer no);

    void mockOtherTimeOutTransactional(Integer no);

    void tableLock();
}

package com.example.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.mapper.UserAccountMapper;
import com.example.model.po.UserAccount;
import com.example.service.UserAccountService;
import com.google.common.base.Functions;
import com.google.common.collect.Lists;

import cn.hutool.core.lang.UUID;
import lombok.extern.slf4j.Slf4j;

/**
 * @author c-zhongwh01
 * @description 针对表【t_user_account(用户账户表)】的数据库操作Service实现
 * @createDate 2024-03-11 22:25:55
 */
@Slf4j
@Service
public class UserAccountServiceImpl extends ServiceImpl<UserAccountMapper, UserAccount> implements UserAccountService {

    @Override
    public void saveUserAccount(Integer count) {
        List<UserAccount> userAccounts = this.buildTestDate(count);
        this.saveBatch(userAccounts);
    }

    private List<UserAccount> buildTestDate(Integer count) {
        List<UserAccount> list = Lists.newArrayList();
        UserAccount one = lambdaQuery().orderByDesc(UserAccount::getId).last("limit 1").one();
        long beginId = one == null ? 0 : one.getId();
        for (int i = 0; i < count; i++) {
            if (one == null) {
                beginId = i;
            }
            UserAccount userAccount = new UserAccount();
            userAccount.setName("name-" + beginId);
            userAccount.setMobile(String.valueOf((12345678900L + beginId)));
            userAccount.setAccount(UUID.randomUUID().toString(true));
            userAccount.setAmount(new BigDecimal("1000"));
            list.add(userAccount);
        }
        return list;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deadLock() {
        try {
            UserAccount account = this.getById(1L);
            BigDecimal decimal = account.getAmount().subtract(new BigDecimal("100"));
            this.updateAmountById(account.getId(), decimal);
            TimeUnit.SECONDS.sleep(5);

        } catch (Exception e) {
            log.info("++++++++++++++异常");
            throw new RuntimeException(e);
        }

    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void mockOtherTransactional() {
        try {
            UserAccount account = this.getById(2L);
            BigDecimal decimal = account.getAmount().subtract(new BigDecimal("300"));
            this.updateAmountById(account.getId(), decimal);
            TimeUnit.SECONDS.sleep(10);

            UserAccount account2 = this.getById(1L);
            BigDecimal decimal2 = account2.getAmount().add(new BigDecimal("300"));
            this.updateAmountById(account2.getId(), decimal2);
        } catch (Exception e) {
            log.info("-------------异常");
            throw new RuntimeException(e);
        }
    }

    private void updateAmountById(Long id, BigDecimal decimal) {
        UserAccount update = new UserAccount();
        update.setId(id);
        update.setAmount(decimal);
        this.updateById(update);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deadLockSolutionOne() {
        List<Long> ids = Lists.newArrayList(1L, 2L);
        try {
            // List<UserAccount> list = lambdaQuery().in(UserAccount::getId, ids).last("LOCK IN SHARE MODE").list();
            List<UserAccount> list = lambdaQuery().in(UserAccount::getId, ids).last("for update").list();
            Map<Long, UserAccount> map = list.stream().collect(Collectors.toMap(UserAccount::getId, Functions.identity()));

            UserAccount account = map.get(1L);
            BigDecimal decimal = account.getAmount().subtract(new BigDecimal("100"));
            this.updateAmountById(account.getId(), decimal);
            TimeUnit.SECONDS.sleep(5);

            UserAccount account2 = map.get(2L);
            BigDecimal decimal2 = account2.getAmount().add(new BigDecimal("100"));
            this.updateAmountById(account2.getId(), decimal2);
            // TimeUnit.SECONDS.sleep(60);
        } catch (Exception e) {
            log.info("-------------异常");
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void mockOtherTransactionalSolutionOne() {
        List<Long> ids = Lists.newArrayList(1L, 2L);
        try {
            // List<UserAccount> list = lambdaQuery().in(UserAccount::getId, ids).last("LOCK IN SHARE MODE").list();
            List<UserAccount> list = lambdaQuery().in(UserAccount::getId, ids).last("for update").list();
            Map<Long, UserAccount> map = list.stream().collect(Collectors.toMap(UserAccount::getId, Functions.identity()));

            UserAccount account = map.get(2L);
            BigDecimal decimal = account.getAmount().subtract(new BigDecimal("300"));
            this.updateAmountById(account.getId(), decimal);
            TimeUnit.SECONDS.sleep(10);
            UserAccount account2 = map.get(1L);
            BigDecimal decimal2 = account2.getAmount().add(new BigDecimal("300"));
            this.updateAmountById(account2.getId(), decimal2);
        } catch (Exception e) {
            log.info("-------------异常");
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void timeOut(Integer no) {
        try {
            if (no == 1) {
                this.timeOutOne();
            } else if (no == 2) {
                this.updateAmountById(3L, new BigDecimal("500"));
                TimeUnit.SECONDS.sleep(60);
            }
        } catch (Exception e) {
            log.info("-------------异常");
            throw new RuntimeException(e);
        }
    }

    private void timeOutOne() throws InterruptedException {
        UserAccount one = lambdaQuery().orderByDesc(UserAccount::getId).last("limit 1").one();
        Long id;
        if (one == null) {
            id = 1L;
        } else {
            id = one.getId() + 1L;
        }
        this.updateAmountById(id, new BigDecimal("500"));
        TimeUnit.SECONDS.sleep(60);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void mockOtherTimeOutTransactional(Integer no) {
        try {
            if (no == 1) {
                List<UserAccount> userAccounts = this.buildTestDate(1);
                this.saveBatch(userAccounts);
            } else if (no == 2) {
                // 需要设置实体类 @TableId(value = "id", type = IdType.INPUT)
                UserAccount userAccount = new UserAccount();
                userAccount.setId(4L);
                userAccount.setName("name-" + 4);
                userAccount.setMobile(String.valueOf((12345678900L + 4L)));
                userAccount.setAccount(UUID.randomUUID().toString(true));
                userAccount.setAmount(new BigDecimal("1000"));
                this.save(userAccount);
            }
        } catch (Exception e) {
            log.info("-------------异常");
            throw new RuntimeException(e);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class, timeout = 10)
    public void tableLock() {
        try {
            lambdaUpdate().set(UserAccount::getAmount, new BigDecimal("1200")).eq(UserAccount::getName, "undefined").update();
            TimeUnit.SECONDS.sleep(60);
        } catch (Exception e) {
            log.info("-------------异常");
            throw new RuntimeException(e);
        }

    }
}

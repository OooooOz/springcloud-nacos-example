package com.example.controller.content;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.response.BaseResponse;
import com.example.service.UserAccountService;

@RestController
@RequestMapping("account")
public class UserAccountController {

    @Autowired
    private UserAccountService userAccountService;

    @PostMapping("/save")
    public BaseResponse saveUserAccount(Integer count) {
        userAccountService.saveUserAccount(count);
        return BaseResponse.SUCCESS();
    }

    @PutMapping("/dead/lock")
    public BaseResponse deadLock() {
        CompletableFuture.runAsync(() -> userAccountService.deadLock());
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        userAccountService.mockOtherTransactional();
        return BaseResponse.SUCCESS();
    }

    @PutMapping("/dead/lock/solution/{no}")
    public BaseResponse deadLockSolution(@PathVariable("no") Integer no) {
        CompletableFuture.runAsync(() -> {
            if (no == 1) {
                userAccountService.deadLockSolutionOne();
            }
        });
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (no == 1) {
            userAccountService.mockOtherTransactionalSolutionOne();
        }
        return BaseResponse.SUCCESS();
    }

    @PutMapping("/time/out")
    public BaseResponse timeOut(@RequestParam("no") Integer no) {
        CompletableFuture.runAsync(() -> userAccountService.timeOut(no));
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        userAccountService.mockOtherTimeOutTransactional(no);
        return BaseResponse.SUCCESS();
    }

    @PutMapping("/table/lock")
    public BaseResponse tableLock() {
        userAccountService.tableLock();
        return BaseResponse.SUCCESS();
    }

}

package com.example.controller;

import com.example.dto.BaseResponse;
import com.example.dto.UserDTO1;
import com.example.dto.ValidationList;
import org.hibernate.validator.constraints.Length;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@RequestMapping("/api/user1")
@RestController
@Validated
public class UserController1 {

    @PostMapping("/save")
    public BaseResponse saveUser(@RequestBody @Validated(UserDTO1.Save.class) UserDTO1 UserDTO1) {
        // 校验通过，才会执行业务逻辑处理
        return BaseResponse.SUCCESS();
    }

    @PostMapping("/update")
    public BaseResponse updateUser(@RequestBody @Validated(UserDTO1.Update.class) UserDTO1 UserDTO1) {
        // 校验通过，才会执行业务逻辑处理
        return BaseResponse.SUCCESS();
    }

    @PostMapping("/save/list")
    public BaseResponse saveUserList(@RequestBody @Validated(UserDTO1.Save.class) List<UserDTO1> UserDTO1) {
        // 校验通过，才会执行业务逻辑处理
        return BaseResponse.SUCCESS();
    }

    @PostMapping("/save/list/string")
    public BaseResponse saveStringList(@RequestBody @NotEmpty List<@NotEmpty String> strings) {
        // 校验通过，才会执行业务逻辑处理
        return BaseResponse.SUCCESS();
    }

    @PostMapping("/save/list/validation")
    public BaseResponse saveUsers(@RequestBody @Validated(UserDTO1.Save.class) ValidationList<UserDTO1> UserDTO1) {
        // 校验通过，才会执行业务逻辑处理
        return BaseResponse.SUCCESS();
    }

    // 路径变量
    @GetMapping("{userId}")
    public BaseResponse detail(@PathVariable("userId") @Min(10000000000000000L) Long userId) {
        // 校验通过，才会执行业务逻辑处理
        return BaseResponse.SUCCESS();
    }

    // 路径变量
    @DeleteMapping("{userId}")
    public BaseResponse del(@PathVariable("userId") @Min(10000000000000000L) Long userId) {
        // 校验通过，才会执行业务逻辑处理
        return BaseResponse.SUCCESS();
    }

    // 查询参数
    @GetMapping("getByAccount")
    public BaseResponse getByAccount(@Length(min = 6, max = 20) @NotNull String account) {
        // 校验通过，才会执行业务逻辑处理
        return BaseResponse.SUCCESS();
    }
}
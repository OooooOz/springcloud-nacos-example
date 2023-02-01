package com.example.casewhen.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.casewhen.mapper.SysUserMapper;
import com.example.casewhen.po.SysUser;
import com.example.casewhen.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;


@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser>
        implements SysUserService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Override
    public void updateBatchCaseWhen(ArrayList<SysUser> list) {
        long start = System.currentTimeMillis();
        sysUserMapper.updateBatchCaseWhen(list);
        System.out.println("耗时：" + (System.currentTimeMillis() - start));
    }

    @Override
    public void updateForeach(ArrayList<SysUser> list) {
        long start = System.currentTimeMillis();
        sysUserMapper.updateForeach(list);
        System.out.println("耗时：" + (System.currentTimeMillis() - start));
    }
}





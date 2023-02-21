package com.example.casewhen.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.casewhen.po.SysUser;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;

public interface SysUserService extends IService<SysUser> {

    void updateBatchCaseWhen(ArrayList<SysUser> list);

    void updateForeach(ArrayList<SysUser> list);

    void testSimpleExport(HttpServletResponse response);
}

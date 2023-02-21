package com.example.casewhen.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.excel.util.DateUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.casewhen.mapper.SysUserMapper;
import com.example.casewhen.po.SysUser;
import com.example.casewhen.service.SysUserService;
import com.example.casewhen.vo.SysUserExportVO;
import com.example.util.EasyExcelUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


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

    @Override
    public void testSimpleExport(HttpServletResponse response) {
        QueryWrapper<SysUser> wrapper = new QueryWrapper<SysUser>().select("id", "username", "status", "`desc`").last("Limit 1,10");
        List<SysUser> sysUsers = sysUserMapper.selectList(wrapper);
        List<SysUserExportVO> list = BeanUtil.copyToList(sysUsers, SysUserExportVO.class);
        String date = DateUtils.format(new Date(), "yyyyMMddHHmmss");
        EasyExcelUtils.exportExcel("test" + "-" + date + ".xlsx", "sheetName", SysUserExportVO.class, list, response);

    }
}





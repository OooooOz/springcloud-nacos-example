package com.example.casewhen.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.casewhen.po.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;

@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    void updateBatchCaseWhen(@Param("list") ArrayList<SysUser> list);

    void updateForeach(@Param("list") ArrayList<SysUser> list);
}





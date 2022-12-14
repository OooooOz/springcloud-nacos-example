package com.example.dynamic.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@TableName("course_1")
public class Course {
    @TableId(value = "c_id", type = IdType.ASSIGN_ID)
    private Long cId;
    private String cName;
    private Long userId;
    private String cStatus;
}

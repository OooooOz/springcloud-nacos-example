package com.example.dynamic.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("course_1")
public class Course {
    @TableId(type = IdType.ASSIGN_ID)
    private Long cId;
    private String cName;
    private Long userId;
    private String cStatus;
}

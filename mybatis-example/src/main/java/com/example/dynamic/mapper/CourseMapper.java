package com.example.dynamic.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.dynamic.entity.Course;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface CourseMapper extends BaseMapper<Course> {

}

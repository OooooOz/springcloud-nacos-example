package com.example.dynamic.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.dynamic.entity.Course;

/**
 * @author Mr.zhong
 * @description 针对表【course_1】的数据库操作Service
 * @createDate 2022-12-14 13:42:02
 */
public interface CourseService extends IService<Course> {

    void add(Course course);
}

//package com.example.dynamic.controller;
//
//
//import com.baomidou.dynamic.datasource.annotation.DS;
//import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
//import com.example.dynamic.entity.Course;
//import com.example.dynamic.mapper.CourseMapper;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.transaction.annotation.Propagation;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.List;
//import java.util.Random;
//
//@RestController
//@RequestMapping()
//public class ShardingController {
//
//    @Autowired
//    private CourseMapper courseMapper;
//
//    @DS("master")
//    @GetMapping("dynamic/master/add")
//    public String add() {
//        long min = 1;
//        long max = Long.MAX_VALUE;
//        long rangeLong = min + (((long) (new Random().nextDouble() * (max - min))));
//        Course course = Course.builder().cName("java").cStatus("Normal").userId(rangeLong).build();
//        courseMapper.insert(course);
//        return "success";
//    }
//
//    @DS("slave")
//    @GetMapping("dynamic/slave/list")
//    public Object list() {
//        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<Course>();
//        List<Course> courses = courseMapper.selectList(wrapper);
//        return courses;
//    }
//
//
//    @GetMapping("dynamic/master/add1")
//    public String add1() {
//        long min = 1;
//        long max = Long.MAX_VALUE;
//        long rangeLong = min + (((long) (new Random().nextDouble() * (max - min))));
//        Course course = Course.builder().cName("java").cStatus("Normal").userId(rangeLong).build();
//        insertM(course);
//        list();
//        int a = 1 / 0;
//        return "success";
//    }
//
//    @DS("slave")
//    @Transactional(propagation = Propagation.REQUIRES_NEW)
//    public void insertS(Course course) {
//        System.out.println("slave ======================");
//        courseMapper.insert(course);
//    }
//
//    @DS("master")
//    @Transactional()
//    public void insertM(Course course) {
//        System.out.println("master ======================");
//
//        courseMapper.insert(course);
//    }
//}

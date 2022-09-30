package com.example.sharding.controller;

import com.example.sharding.entity.Course;
import com.example.sharding.mapper.CourseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController
@RequestMapping()
public class ShardingController {

    @Autowired
    private CourseMapper courseMapper;

    @GetMapping("/sharding/add")
    public String add() {
        long min = 1;
        long max = Long.MAX_VALUE;
        long rangeLong = min + (((long) (new Random().nextDouble() * (max - min))));
        Course course = Course.builder().cName("java").cStatus("Normal").userId(rangeLong).build();
        courseMapper.insert(course);
        return "success";
    }
}

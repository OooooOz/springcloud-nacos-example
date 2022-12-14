package com.example.dynamic.transactional;


import com.example.dynamic.entity.Course;
import com.example.dynamic.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController
@RequestMapping()
public class TransactionalController {

    @Autowired
    private CourseService courseService;

    @GetMapping("transactional/master/add")
    public String add() {
        long min = 1;
        long max = Long.MAX_VALUE;
        long rangeLong = min + (((long) (new Random().nextDouble() * (max - min))));
        Course course = Course.builder().cName("java").cStatus("Normal").userId(rangeLong).build();
        courseService.add(course);
        return "success";
    }
}

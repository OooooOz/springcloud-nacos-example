package com.example.dynamic.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.dynamic.entity.Course;
import com.example.dynamic.mapper.CourseMapper;
import com.example.dynamic.service.CourseService;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

/*
同类中（自调用，方法A调用方法B）：
    没有注解的方法A调用有注解的方法B，A和B都不会回滚；因为方法A没有注解，所以调用的时候通过目标对象进行反射调用，即便方法B有注解，那也是目标对象的内部调用，并没有通过方法代理MethodInvocation进行调用，所以不生效
    有注解的方法A调用没有注解的方法B，A和B都会回滚，因为方法A是通过方法代理调用的，方法B虽然没有注解，但是和A是在同一个事务上
    有注解的方法A调用没有注解的方法B，A和B都会回滚，即便方法B传播行为是Require_NEW也不会创建新的事务，还是用A的事务

    对于自调用的解决方方案可以将另外的事务方法B抽到一个独立的类，这样便可以通过代理调用，事务生效；或者通过注入自己，通过注入对象进行方法B调用；还可以从容器中获取类，再进行调用
*/

@Service
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course>
        implements CourseService {

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;

    @Override
    @Transactional
    public void add(Course course) {
        courseMapper.insert(course);
        add1();
    }

    @Transactional
    public void add1() {
        long min = 1;
        long max = Long.MAX_VALUE;
        long rangeLong = min + (((long) (new Random().nextDouble() * (max - min))));
        Course course = Course.builder().cName("java").cStatus("Normal1").userId(rangeLong).build();
        courseMapper.insert(course);
        int a = 1 / 0;
    }

    /*
    在batch模式重复使用已经预处理的语句，并且批量执行所有更新语句，显然batch性能将更优；
    但batch模式也有自己的问题，比如在Insert操作时，在事务没有提交之前，是没有办法获取到自增的id，这在某型情形下是不符合业务要求的
    插入大量数据时，效率最高，通过重复使用预编译后的语句，不断填充数据，批量提交（数据量很大使用）
*/
    public void add(List<Course> itemList) {

        // 新获取一个模式为BATCH，自动提交为false的session
        // 如果自动提交设置为true,将无法控制提交的条数，改为最后统一提交，可能导致内存溢出
        SqlSession session = sqlSessionTemplate.getSqlSessionFactory().openSession(ExecutorType.BATCH, false);
        CourseMapper mapper = session.getMapper(CourseMapper.class);
        for (int i = 0; i < itemList.size(); i++) {
            mapper.insert(itemList.get(i));
            if (i % 1000 == 999) {//每1000条提交一次防止内存溢出
                session.commit();
                session.clearCache();
            }
        }
        session.commit();
        session.clearCache();
    }
}





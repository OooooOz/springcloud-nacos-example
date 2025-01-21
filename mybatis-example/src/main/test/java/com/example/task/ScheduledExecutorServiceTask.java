package com.example.task;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

/**
 * https://blog.csdn.net/qq_41563912/article/details/126347720
 */
public class ScheduledExecutorServiceTask implements Runnable {

    private final static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(5);

    @Test
    public void test() throws InterruptedException {
        // 延迟不循环任务schedule方法
        scheduler.schedule(new ScheduledExecutorServiceTask(), 2000, TimeUnit.MILLISECONDS);
        // 开启新线程后，junit在主线程运行后会关闭，子线程也就无法运行了。
        TimeUnit.SECONDS.sleep(10);
    }

    @Test
    public void test1() throws InterruptedException {
        // 严格按照一定时间间隔执行
        scheduler.scheduleWithFixedDelay(new ScheduledExecutorServiceTask(), 0, 2000, TimeUnit.MILLISECONDS);
        TimeUnit.SECONDS.sleep(10);
    }


    @Override
    public void run() {
        System.out.println("-----------------------" + System.currentTimeMillis());
    }
}

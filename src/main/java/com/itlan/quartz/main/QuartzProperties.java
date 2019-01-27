package com.itlan.quartz.main;

import java.util.Properties;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

public class QuartzProperties {

    public static void main(String[] args) {
        // 创建工程实例
        StdSchedulerFactory schedulerFactory = new StdSchedulerFactory();
        // 创建配置工厂的属性的对象
        Properties prop = new Properties();
        prop.put(StdSchedulerFactory.PROP_THREAD_POOL_CLASS, "org.quartz.simpl.SimpleThreadPool");
        prop.put("org.quartz.threadPool.threadCount", "5");
        
        try {
            // 加载上面定义的属性
            schedulerFactory.initialize(prop);
             
            Scheduler scheduler = schedulerFactory.getScheduler();
            scheduler.start();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }

    }

}

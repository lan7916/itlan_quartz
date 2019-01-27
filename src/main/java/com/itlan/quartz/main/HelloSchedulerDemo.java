package com.itlan.quartz.main;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import com.itlan.quartz.job.HelloJob;

public class HelloSchedulerDemo {

    public static void main(String[] args) throws Exception {
        // 1、调度器（Scheduler），从工厂中获取调度的实例（默认：实例化new StdSchedulerFactory();）
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

        // 2、任务实例（JobDetail）定义一个任务调度实例，将该实例与HelloJob绑定，任务类需要实现Job接口
        JobDetail jobDetail = JobBuilder.newJob(HelloJob.class) // 加载任务类，与HelloJob完成绑定，要求HelloJob实现Job接口
                .withIdentity("job1", "group1") // 参数1：任务的名称（唯一实例）；参数2：任务组的名称
                .usingJobData("message", "打印日志") // 传递参数，名称message
                .usingJobData("count", 0)
                .build();

//        System.out.println("name:" +jobDetail.getKey().getName());
//        System.out.println("group:" +jobDetail.getKey().getGroup()); // 如果没有指定组名：DEFAULT
//        System.out.println("jobClass:" +jobDetail.getJobClass().getName());

        // 3、触发器（Trigger）定义触发器，马上执行，然后每5秒重复执行一次
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("trigger1", "group1") // 参数1：触发器的名称（唯一实例）；参数2：触发器组的名称
                .startNow() // 马上启动触发器
                //.startAt(triggerStartTime) // 针对某个时刻执行
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().repeatSecondlyForever(5)) // 每5秒重复执行一次
                .usingJobData("message", "simple触发器") // 传递参数，名称message
                .build();
        // 4、让调度器关联任务和触发器，保证按照触发器定义的调整执行任务
        scheduler.scheduleJob(jobDetail, trigger);

        // 5、启动
        scheduler.start();
        // 关闭
        //scheduler.shutdown();

    }

}

package com.itlan.quartz.main;

import java.util.Date;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import com.itlan.quartz.job.HelloJobSimpleTrigger;

public class HelloSchedulerDemoSimpleTrigger {

    public static void main(String[] args) throws Exception {
        // 1、调度器（Scheduler），从工厂中获取调度的实例（默认：实例化new StdSchedulerFactory();）
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
        
        // 设置任务的开始时间
        Date startDate = new Date();
        // 任务的开始时间推迟3秒
        startDate.setTime(startDate.getTime() +3000);
        // 设置任务的结束时间
        Date endDate = new Date();
        // 任务的开始时间推迟3秒
        endDate.setTime(endDate.getTime() +30000);

        // 2、任务实例（JobDetail）定义一个任务调度实例，将该实例与HelloJobSimpleTrigger绑定，任务类需要实现Job接口
        JobDetail jobDetail = JobBuilder.newJob(HelloJobSimpleTrigger.class) // 加载任务类，与HelloJob完成绑定，要求HelloJob实现Job接口
                .withIdentity("job1", "group1") // 参数1：任务的名称（唯一实例）；参数2：任务组的名称
                .build();

        // 3、触发器（Trigger）定义触发器，马上执行，然后每5秒重复执行一次
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("trigger1", "group1") // 参数1：触发器的名称（唯一实例）；参数2：触发器组的名称
                .startAt(startDate) // 设置任务的开始时间
                //.endAt(endDate) // 设置任务的结束时间
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().repeatSecondlyForever(5).withRepeatCount(2))  // 每5秒执行一次，连续执行3次后停止，默认是0
                //.startAt(startDate) // 设置任务的开始时间
                .build();
        // 4、让调度器关联任务和触发器，保证按照触发器定义的调整执行任务
        scheduler.scheduleJob(jobDetail, trigger);

        // 5、启动
        scheduler.start();
        // 关闭
        //scheduler.shutdown();
    }

}

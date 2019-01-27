package com.itlan.quartz.main;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import com.itlan.quartz.job.HelloJobScheduler;

public class HelloSchedulerDemoScheduler {

    public static void main(String[] args) throws Exception {
        // 1、调度器（Scheduler），从工厂中获取调度的实例（默认：实例化new StdSchedulerFactory();）
        // Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
        SchedulerFactory schedulerFactory = new StdSchedulerFactory();
        Scheduler scheduler = schedulerFactory.getScheduler();

        // 2、任务实例（JobDetail）定义一个任务调度实例，将该实例与HelloJobSimpleTrigger绑定，任务类需要实现Job接口
        JobDetail jobDetail = JobBuilder.newJob(HelloJobScheduler.class) // 加载任务类，与HelloJob完成绑定，要求HelloJob实现Job接口
                .withIdentity("job1", "group1") // 参数1：任务的名称（唯一实例）；参数2：任务组的名称
                .build();

        // 3、触发器（Trigger）定义触发器，马上执行，然后每5秒重复执行一次
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("trigger1", "group1") // 参数1：触发器的名称（唯一实例）；参数2：触发器组的名称
                .startNow() // 马上开始
                .withSchedule(CronScheduleBuilder.cronSchedule("* * * * * ?"))  // 日历
                .build();

        // 4、让调度器关联任务和触发器，保证按照触发器定义的调整执行任务
        Date date = scheduler.scheduleJob(jobDetail, trigger);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println("调度器的开始时间是： " +dateFormat.format(date));

        // 5、启动
        scheduler.start();
        // Scheduler执行2秒后挂起
        Thread.sleep(2000L);
        /**
         * shutdown(true)：表示等待所有正在执行的Job执行完毕之后，再关闭Scheduler
         * shutdown(false)： 表示直接关闭Scheduler
         */
        scheduler.shutdown(false);
        System.out.println("Scheduler是否被关闭：" +scheduler.isShutdown());
        // 挂起
        scheduler.standby();
        // Scheduler执行5秒后自动开启
        Thread.sleep(5000L);
        // 重新启动任务
        scheduler.start();
        // 关闭
        //scheduler.shutdown();
    }

}

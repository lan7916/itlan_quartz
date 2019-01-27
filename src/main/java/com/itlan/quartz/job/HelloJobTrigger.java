package com.itlan.quartz.job;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Trigger;

// 定义任务类
public class HelloJobTrigger implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        // 输出当前时间
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = dateFormat.format(date);
        // 工作内容
        System.out.println("正在进行数据库的备份工作，备份数据库的时间是：" +dateString);
        
        // 获取jobKey、startTime、endTime
        Trigger trigger = context.getTrigger();
        System.out.println("jobKey的名称：" +trigger.getJobKey().getName() +";    jobKey的组名称：" +trigger.getJobKey().getGroup());
        System.out.println("任务的开始时间：" +dateFormat.format(trigger.getStartTime()) +";    任务的结束时间：" +dateFormat.format(trigger.getEndTime()));
    }

}

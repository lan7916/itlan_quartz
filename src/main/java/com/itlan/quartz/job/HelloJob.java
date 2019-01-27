package com.itlan.quartz.job;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.PersistJobDataAfterExecution;
import org.quartz.TriggerKey;

// 定义任务类
@PersistJobDataAfterExecution  // 多次调用Job的时候，会对Job进行持久化，即保存一个数据的信息
public class HelloJob implements Job {
    private String message;
    private Integer count;
    
//    public HelloJob() {
//        System.out.println("欢迎访问HelloJob！");
//    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        // 输出当前时间
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = dateFormat.format(date);
        
        // 获取JobDetail的内容
        JobKey jobKey = context.getJobDetail().getKey();
        System.out.println("工作任务名称：" +jobKey.getName() +"；    工作任务组：" +jobKey.getGroup());
        System.out.println("任务类名称（带包名）：" +context.getJobDetail().getJobClass().getName());
        System.out.println("任务类名称：" +context.getJobDetail().getJobClass().getSimpleName());
        
//        // 从JobDetail对象中获取JobDataMap的数据
//        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
//        String jobDataMessage = jobDataMap.getString("message");
//        System.out.println("任务数据的参数值：" +jobDataMessage);
//        // 从Trigger对象中获取JobDataMap的数据
//        JobDataMap triggerDataMap = context.getTrigger().getJobDataMap();
//        String triggerDataMessage = triggerDataMap.getString("message");
//        System.out.println("触发器数据的参数值：" +triggerDataMessage);
        // 从setter方法中读取message的值
        System.out.println("参数值：" +message);
        // 获取Trigger的内容
        TriggerKey triggerKey = context.getTrigger().getKey();
        System.out.println("触发器名称：" +triggerKey.getName() +"；    触发器组：" +triggerKey.getGroup());
        
        // 获取其他的内容
        System.out.println("当前任务执行时间：" +dateFormat.format(context.getFireTime()));
        System.out.println("下一任务执行时间：" +dateFormat.format(context.getNextFireTime()));
        
        // 工作内容
        System.out.println("正在进行数据库的备份工作，备份数据库的时间是：" +dateString);

        // 输出count
        ++count;  // 累加count
        // 将count存放到JobDataMap中
        System.out.println("count的数量：" +count);
        context.getJobDetail().getJobDataMap().put("count", count);
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

}

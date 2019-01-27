package com.itlan.quartz.listener;

import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.SchedulerListener;
import org.quartz.Trigger;
import org.quartz.TriggerKey;

public class MySchedulerListener implements SchedulerListener {

    @Override
    public void jobScheduled(Trigger trigger) {
        String name = trigger.getKey().getName();
        // 用于部署JobDetail时调用
        System.out.println(name +" 完成部署");
    }

    @Override
    public void jobUnscheduled(TriggerKey triggerKey) {
        String name = triggerKey.getName();
        // 用于卸载JobDetail时调用
        System.out.println(name +" 完成卸载");
    }

    @Override
    public void triggerFinalized(Trigger trigger) {
        String name = trigger.getKey().getName();
        // 当一个Trigger来到了再也不会触发的状态时调用这个方法。除非这个Job已设置成了持久性，否则它就会从Scheduler中移除。
        System.out.println(name +" 触发器被移除");
    }

    @Override
    public void triggerPaused(TriggerKey triggerKey) {
        String name = triggerKey.getName();
        // Scheduler调用这个方法是发生在一个Trigger或Trigger组被暂停时。假如是Trigger组的话，triggerName参数将为null。
        System.out.println(name +" 正在被暂停");
    }

    @Override
    public void triggersPaused(String triggerGroup) {
        // Scheduler调用这个方法是发生在一个Trigger或Trigger组被暂停时。假如是Trigger组的话，triggerName参数将为null。
        System.out.println("触发器组" +triggerGroup +" 正在被暂停");
    }

    @Override
    public void triggerResumed(TriggerKey triggerKey) {
        // Scheduler调用这个方法是发生在一个Trigger或Trigger组从暂停中恢复时。假如是Trigger组的话，triggerName参数将为null。参数将为null。
        String name = triggerKey.getName();
        System.out.println(name +" 正在从暂停中恢复");
    }

    @Override
    public void triggersResumed(String triggerGroup) {
        // Scheduler调用这个方法是发生在一个Trigger或Trigger组从暂停中恢复时。假如是Trigger组的话，triggerName参数将为null。参数将为null。
        System.out.println("触发器组" +triggerGroup +" 正在从暂停中恢复");
    }

    @Override
    public void jobAdded(JobDetail jobDetail) {
        // 
        System.out.println(jobDetail.getKey() +" 添加工作任务");
    }

    @Override
    public void jobDeleted(JobKey jobKey) {
        // 
        System.out.println(jobKey +" 删除工作任务");
    }

    @Override
    public void jobPaused(JobKey jobKey) {
        // 
        System.out.println(jobKey +" 工作任务正在被暂停");
    }

    @Override
    public void jobsPaused(String jobGroup) {
        // 
        System.out.println("工作组" +jobGroup +" 正在被暂停");
    }

    @Override
    public void jobResumed(JobKey jobKey) {
        // 
        System.out.println(jobKey +" 正在从暂停中恢复");
    }

    @Override
    public void jobsResumed(String jobGroup) {
        // 
        System.out.println("工作组" +jobGroup +" 正在从暂停中恢复");
    }

    @Override
    public void schedulerError(String msg, SchedulerException cause) {
        // 在Scheduler的正常运行期间产生一个严重错误时调用这个方法。
        System.out.println("产生严重错误的时候调用" +msg +"    " +cause.getUnderlyingException());
    }

    @Override
    public void schedulerInStandbyMode() {
        // 当Scheduler处于StandBy模式时，调用该方法。
        System.out.println("调度器被挂起模式的时候调用");
    }

    @Override
    public void schedulerStarted() {
        // 当Scheduler开启时，调用该方法
        System.out.println("调度器开启的时候调用");
    }

    @Override
    public void schedulerStarting() {
        // 
        System.out.println("调度器正在开启的时候调用");
    }

    @Override
    public void schedulerShutdown() {
        // 
        System.out.println("调度器关闭的时候调用");
    }

    @Override
    public void schedulerShuttingdown() {
        // 
        System.out.println("调度器正在关闭的时候调用");
    }

    @Override
    public void schedulingDataCleared() {
        // 当Scheduler中的数据被清除时，调用该方法
        System.out.println("调度器数据被清除的时候调用");
    }

}

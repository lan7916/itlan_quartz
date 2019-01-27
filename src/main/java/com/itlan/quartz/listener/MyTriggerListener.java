package com.itlan.quartz.listener;

import org.quartz.JobExecutionContext;
import org.quartz.Trigger;
import org.quartz.Trigger.CompletedExecutionInstruction;
import org.quartz.TriggerListener;

public class MyTriggerListener implements TriggerListener {
    
//    private String name;
//    // 构造方法，自定义传递触发器的名称，默认是类的名称
//    public MyTriggerListener(String name) {
//        super();
//        this.name = name;
//    }
//    @Override
//    public String getName() {
//        return this.name;  // 不返还会抛出一个名称为空的异常
//    }

    @Override
    public String getName() {
        String name = this.getClass().getSimpleName();
        System.out.println("触发器的名称：" +name);
        return name;  // 不返还会抛出一个名称为空的异常
    }

    @Override
    public void triggerFired(Trigger trigger, JobExecutionContext context) {
        String name = this.getClass().getSimpleName();
        System.out.println(name +"被触发");
    }

    @Override
    public boolean vetoJobExecution(Trigger trigger, JobExecutionContext context) {
        String name = this.getClass().getSimpleName();
        // TriggerListener给了一个选择去否决Job的执行。假如这个方法返回true，这个Job将不会为此次Trigger触发而得到执行。
        System.out.println(name +" 没有被触发");
        return false;  // true:表示不会执行Job的方法
    }

    @Override
    public void triggerMisfired(Trigger trigger) {
        String name = this.getClass().getSimpleName();
        // Scheduler调用这个方法是在Trigger错过触发时
        System.out.println(name +" 错过触发");
    }

    @Override
    public void triggerComplete(Trigger trigger, JobExecutionContext context,
            CompletedExecutionInstruction triggerInstructionCode) {
        String name = this.getClass().getSimpleName();
        // Trigger被触发并且完成了Job的执行时，Scheduler调用这个方法。
        System.out.println(name +" 完成之后触发");
    }

}

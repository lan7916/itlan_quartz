# Quartz任务调度

lan7916 <lan7916@hotmail.com>

v1.0, 2019.01

前言
----
最近学习“黑马程序员”的《精品详解Quartz视频》教程，顺便将课程的笔记整理出来。发现网上根本搜不到视频的笔记资料，遂将此资料分享出来，若有侵权，请联系本人<lan7916@hotmail.com>
----

教程 https://github.com/lan7916/itlan_quartz[源代码]

## 一、Quartz概念
Quartz是OpenSymphony开源组织在Job scheduling领域又一个开源项目，它可以与J2EE与J2SE应用程序相结合，也可以单独使用。

Quartz是开源且具有丰富特性的“任务调度库”，能够集成于任何的Java应用，小到独立的应用，大至电子商业系统。Quartz能够创建亦简单亦复杂的调度，以执行上十、上百，甚至上万的任务。任务job被定义为标准的Java组件，能够执行任何你想要实现的功能。Quartz调度框架包含许多企业级的特性，如JTA事务、集群的支持。

简而言之，Quartz就是基于Java实现的任务调度框架，用于执行你想要执行的任何任务。

官网： http://www.quartz-scheduler.org[http://www.quartz-scheduler.org]

## 二、Quartz运行环境
* Quartz可以运行嵌入在另一个独立式应用程序
* Quartz可以在应用程序服务器（或Servlet容器）内被实例化，并且参与事务
* Quartz可以作为一个独立的程序运行（其自己的Java虚拟机内），可以通过RMI使用
* Quartz可以被实例化，作为独立的项目集群（负载平衡和故障转移功能），用于作业的执行

## 三、Quartz设计模式
* Builder模式
* Factory模式
* 组件模式
* 链式编程

## 四、Quartz学习的核心概念
* 任务Job

Job就是你想要实现的任务类，每一个Job必须实现org.quartz.job接口，且只需实现接口定义的execute()方法。

* 触发器Trigger

Trigger为你执行任务的触发器，比如你想每天定时3点发送一份统计邮件，Trigger将会设置3点执行该任务。 +
Trigger主要包含两种SimplerTrigger和CronTrigger两种。关于二者的区别和使用场景，后续课程会进行讨论。

* 调度器Scheduler

Scheduler为任务的调度器，它会将任务Job及触发器Trigger整合起来，负责基于Trigger设定的时间来执行Job。

## 五、Quartz的体系结构
image::imgs/01.jpg[]

## 六、Quartz的几个常用API
以下是Quartz编程API几个重要接口，也是Quartz的重要组件。

* Scheduler 用于与调度程序交互的主程序接口。 +
Scheduler调度程序-任务执行计划表，只有安排进执行计划的任务Job（通过scheduler.scheduleJob方法安排进执行计划），当它预先定义的执行时间到了的时候（任务触发Trigger），该任务才会执行。

* Job 我们预先定义的希望在未来时间能被调度程序执行的任务类，我们可以自定义。

* JobDetail 使用JobDetail来定义定时任务的实例，JobDetail实例是通过JobBuilder类创建的。

* JobDataMap 可以包含不限量的（序列化的）数据对象，在Job实例执行的时候，可以使用其中的数据；JobDataMap是Java Map接口的一个实现，额外增加了一些便于存取基本类型的数据的方法。

* Trigger 触发器，Trigger对象是用来触发执行Job的。当调度一个Job时，我们实例一个触发器，然后调整它的属性来满足Job执行的条件。表明任务在什么时候会执行。定义了一个已经被安排的任务将会在什么时候执行的时间条件，比如每2秒就执行一次。

* JobBuilder 用于声明一个任务实例，也可以定义关于该任务的详情，比如任务名、组名等，这个声明的实例将会作为一个实际执行的任务。

* TriggerBuilder 触发器创建器，用于创建触发器Trigger实例。

* JobListener、TriggerListener、SchedulerListener监听器，用于对组件的监听。

## 七、Quart的使用
### 1、准备工作
新建maven工程

image::imgs/02.jpg[]
image::imgs/03.jpg[]

查找： https://mvnrepository.com/[https://mvnrepository.com/]

image::imgs/04.jpg[]

### 2、引入Quartz的jar包
```xml
<dependencies>
    <!-- Quartz 核心包 -->
    <dependency>
        <groupId>org.quartz-scheduler</groupId>
        <artifactId>quartz</artifactId>
        <version>2.3.0</version>
    </dependency>
    <!-- Quartz 工具包 -->
    <dependency>
        <groupId>org.quartz-scheduler</groupId>
        <artifactId>quartz-jobs</artifactId>
        <version>2.3.0</version>
    </dependency>
    <!-- log4j -->
    <dependency>
        <groupId>org.slf4j</groupId>
        <artifactId>slf4j-log4j12</artifactId>
        <version>1.7.25</version>
    </dependency>
    <dependency>
        <groupId>log4j</groupId>
        <artifactId>log4j</artifactId>
        <version>1.2.17</version>
    </dependency>
</dependencies>
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.7.0</version>
            <configuration>
                <target>1.8</target>
                <source>1.8</source>
            </configuration>
        </plugin>
    </plugins>
</build>
```

导入log4j.properties日志文件
[source,properties]
----
### direct log messages to stdout ###
log4j.appender.stdout=org.apache.log4j.ConsoleAppender
log4j.appender.stdout.Target=System.out
log4j.appender.stdout.layout=org.apache.log4j.PatternLayout
log4j.appender.stdout.layout.ConversionPattern=%d{ABSOLUTE} %5p %c{1}:%L - %m%n

### direct log file mylog.log ###
log4j.appender.file=org.apache.log4j.FileAppender
log4j.appender.file.File=./mylog.log
log4j.appender.file.layout=org.apache.log4j.PatternLayout
log4j.appender.file.layout.ConversionPattern=%d{ABSOLUTE} %5p %c{1}:%L - %m%n

*** set log levels - for more verbose logging change 'info' to 'debug' ###

log4j.rootLogger=info, stdout
----
### 3、入门案例
（1）创建HelloJob任务类

image::imgs/05.jpg[]
HelloJob.java
[source,java]
----
// 定义任务类
public class HelloJob implements Job {

    @Override
    public void execute(JobExecutionContext arg0) throws JobExecutionException {
        // 输出当前时间
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = dateFormat.format(date);
        // 工作内容
        System.out.println("正在进行数据库的备份工作，备份数据库的时间是：" +dateString);
    }
}
----

（2）创建任务调度类HelloSchedulerDemo +
HelloSchedulerDemo.java
[source,java]
----
public class HelloSchedulerDemo {

    public static void main(String[] args) throws Exception {
        // 1、调度器（Scheduler），从工厂中获取调度的实例（默认：实例化new StdSchedulerFactory();）
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

        // 2、任务实例（JobDetail）定义一个任务调度实例，将该实例与HelloJob绑定，任务类需要实现Job接口
        JobDetail jobDetail = JobBuilder.newJob() // 加载任务类，与HelloJob完成绑定，要求HelloJob实现Job接口
                .withIdentity("job1", "group1") // 参数1：任务的名称（唯一实例）；参数2：任务组的名称
                .build();

        // 3、触发器（Trigger）定义触发器，马上执行，然后每5秒重复执行一次
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("trigger1", "group1") // 参数1：触发器的名称（唯一实例）；参数2：触发器组的名称
                .startNow() // 马上启动触发器
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().repeatSecondlyForever(5)) // 每5秒重复执行一次
                .build();

        // 4、让调度器关联任务和触发器，保证按照触发器定义的调整执行任务
        scheduler.scheduleJob(jobDetail, trigger);

        // 5、启动
        scheduler.start();
        // 关闭
        //scheduler.shutdown();
    }

}
----

（3）实现效果

image::imgs/06.jpg[]

### 4、Job和JobDetail介绍
* Job：工作任务调度的接口，任务了需要实现该接口。该接口中定义execute方法，类似JDK提供的TimeTask类的run方法。在里面编写任务执行的业务逻辑。
* Job实例在Quartz中的声明周期：每次调度器执行Job时，它在调用execute方法前会创建一个新的Job实例，当调用完成后，关联的Job对象实例会被释放，释放的实例会被垃圾回收机制回收。
* JobDetail：JobDetail为Job实例提供了许多设置属性，以及JobDataMap成员变量属性，它用来存储特定Job实例的状态信息，调度器需要借助JobDetail对象来添加Job实例。
* JobDetail重要属性：name、group、jobClass、JobDataMap

[source,java]
----
JobDetail job = JobBuilder.newJob(HelloJob.class)
        .withIdentity("job1", "group1") // 定义该实例唯一标识，并指定一个组
        .build();

System.out.println("name:" +job.getKey().getName());
System.out.println("group:" +job.getKey().getGroup());
System.out.println("jobClass:" +job.getJobClass().getName());
----

### 5、JobExecutionContext介绍
* 当Scheduler调用一个Job，就会将JobExecutionContext传递给Job的execute()方法；
* Job能通过JobExecutionContext对象访问到Quartz运行时候的环境以及Job本身的明细数据。

### 6、JobDataMap介绍
（1）使用Map获取

* 在进行任务调度时，JobDataMap存储在JobExecutionContext中，非常方便获取。
* JobDataMap可以用来装载任何可序列化的数据对象，当Job实例对象被执行时这些参数对象会传递给它。
* JobDataMap实现了JDK的Map接口，并且添加了非常方便的方法用来存取基本数据类型。

HelloSchedulerDemo.java
[source,java]
----
// 2：任务实例（JobDetail）定义一个任务调度实例，将该实例与HelloJob绑定，任务类需要实现Job接口
JobDetail job = JobBuilder.newJob(HelloJob.class)
        .withIdentity("job1", "group1") // 定义该实例唯一标识，并指定一个组
        .usingJobData("message", "打印日志")
        .build();

// 3：触发器（Trigger）定义触发器，马上执行，然后每5秒重复执行一次
Trigger trigger = TriggerBuilder.newTrigger()
        .withIdentity("trigger1", "group1") // 定义该实例唯一标识
        .startNow() // 马上启动触发器
        //.startAt(triggerStartTime) // 针对某个时刻执行
        .withSchedule(SimpleScheduleBuilder.simpleSchedule().repeatSecondlyForever(5)) // 每5秒重复执行一次
        .usingJobData("message", "simple触发器")
        .build();
----
HelloJob.java
[source,java]
----
JobKey jobKey = context.getJobDetail().getKey();
System.out.println("工作任务名称：" +jobKey.getName() +"；    工作任务组：" +jobKey.getGroup());
System.out.println("任务类名称（带包名）：" +context.getJobDetail().getJobClass().getName());
System.out.println("任务类名称：" +context.getJobDetail().getJobClass().getSimpleName());
System.out.println("当前任务执行时间：" +context.getFireTime());
System.out.println("下一任务执行时间：" +context.getNextFireTime());

TriggerKey triggerKey = context.getTrigger().getKey();
System.out.println("触发器名称：" +triggerKey.getName() +"；    触发器组：" +triggerKey.getGroup());

JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
String jobDataMessage = jobDataMap.getString("message");
System.out.println("任务参数消息值：" +jobDataMessage);

JobDataMap triggerDataMap = context.getTrigger().getJobDataMap();
String triggerDataMessage = triggerDataMap.getString("message");
System.out.println("触发器参数消息值：" +triggerDataMessage);
----

（2）Job实现类中添加setter方法对应JobDataMap的键值，Quartz框架默认的JobFactory实现类在初始化Job实例对象时会自动调用这些setter方法。

HelloJob.java
[source,java]
----
private String message;

public void setMessage(String message) {
    this.message = message;
}
----

====
[CAUTION]
**注意：**

如果遇到同名的key，Trigger中的.usingJobData("message", "simple触发器")会覆盖JobDetail中的.usingJobData("message", "打印日志")。
====


### 7、有状态的Job和无状态的Job
@PersistJobDataAfterExecution注解的使用

有状态的Job可以理解为多次Job调用期间可以持有一些状态信息，这些状态信息存储在JobDataMap中，而默认的无状态Job每次调用时都会创建一个新的JobDataMap。

（1）修改HelloSchedulerDemo.java。添加.usingJobData("count", 0)，表示计数器。
[source,java]
----
JobDetail job = JobBuilder.newJob(HelloJob.class)
        .withIdentity("job1", "group1") // 定义该实例唯一标识，并指定一个组
        .usingJobData("message", "打印日志")
        .usingJobData("count", 0)
        .build();
----

（2）修改HelloJob.java

添加count的setter方法。
[source,java]
----
private Integer count;
public void setCount(Integer count) {
    this.count = count;
}
----
在public void execute(JobExecutionContext context) throws JobExecutionException的方法中添加
[source,java]
----
++count;
System.out.println("count的数量：" +count);
context.getJobDetail().getJobDataMap().put("count", count);
----
HelloJob类没有添加@PersistJobDataAfterExecution注解，每次调用时都会创建一个新的JobDataMap。不会累加。

HelloJob类添加@PersistJobDataAfterExecution注解，多次调用期间可以持有一些状态信息，即可以实现count的累加。

### 8、Trigger介绍
image::imgs/07.jpg[]
Quartz有一些不同的触发器类型，不过，用得最多的是SimpleTrigger和CronTrigger。

（1）jobKey +
表示Job实例的标识，触发器被触发时，该指定的Job实例会被执行。

（2）startTime +
表示触发器的时间表，第一次开始被触发的时间，它的数据类型是java.util.Date。

（3）endTime +
指定触发器终止被触发的时间，它的数据类型是java.util.Date。

案例：

HelloJobTrigger.java
[source,java]
----
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
----

HelloSchedulerDemoTrigger.java
[source,java]
----
public class HelloSchedulerDemoTrigger {

    public static void main(String[] args) throws Exception {
        // 1、调度器（Scheduler），从工厂中获取调度的实例（默认：实例化new StdSchedulerFactory();）
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

        // 设置任务的开始时间
        Date startDate = new Date();
        // 任务的开始时间推迟3秒
        startDate.setTime(startDate.getTime() +3000);
        // 设置任务的结束时间
        Date endDate = new Date();
        // 任务的结束时间推迟10秒（10秒后停止）
        endDate.setTime(endDate.getTime() +10000);

        // 2、任务实例（JobDetail）定义一个任务调度实例，将该实例与HelloJobTrigger绑定，任务类需要实现Job接口
        JobDetail jobDetail = JobBuilder.newJob(HelloJobTrigger.class) // 加载任务类，与HelloJob完成绑定，要求HelloJob实现Job接口
                .withIdentity("job1", "group1") // 参数1：任务的名称（唯一实例）；参数2：任务组的名称
                .usingJobData("message", "打印日志") // 传递参数，名称message
                .build();

        // 3、触发器（Trigger）定义触发器，马上执行，然后每5秒重复执行一次
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("trigger1", "group1") // 参数1：触发器的名称（唯一实例）；参数2：触发器组的名称
                //.startNow() // 马上启动触发器
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().repeatSecondlyForever(5)) // 5秒重复执行一次
                .startAt(startDate) // 设置任务的开始时间
                .endAt(endDate)
                .build();
        // 4、让调度器关联任务和触发器，保证按照触发器定义的调整执行任务
        scheduler.scheduleJob(jobDetail, trigger);

        // 5、启动
        scheduler.start();
        // 关闭
        //scheduler.shutdown();
    }

}
----

### 9、SimpleTrigger触发器
SimpleTrigger对于设置和使用是最为简单的一种QuartzTrigger。

它是为那种需要在特定的日期/时间启动，且以一个可能的间隔时间重复执行n此的Job所设计的。

案例一：表示在一个指定的时间段内，执行一次作业任务；

HelloJobSimpleTrigger.java
[source,java]
----
// 定义任务类
public class HelloJobSimpleTrigger implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        // 输出当前时间
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = dateFormat.format(date);
        // 工作内容
        System.out.println("正在进行数据库的备份工作，备份数据库的时间是：" +dateString);
    }

}
----
HelloSchedulerDemoSimpleTrigger.java
[source,java]
----
public class HelloSchedulerDemoSimpleTrigger {

    public static void main(String[] args) throws Exception {
        // 1、调度器（Scheduler），从工厂中获取调度的实例（默认：实例化new StdSchedulerFactory();）
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

        // 设置任务的开始时间
        Date startDate = new Date();
        // 任务的开始时间推迟3秒
        startDate.setTime(startDate.getTime() +3000);

        // 2、任务实例（JobDetail）定义一个任务调度实例，将该实例与HelloJobSimpleTrigger绑定，任务类需要实现Job接口
        JobDetail jobDetail = JobBuilder.newJob(HelloJobSimpleTrigger.class) // 加载任务类，与HelloJob完成绑定，要求HelloJob实现Job接口
                .withIdentity("job1", "group1") // 参数1：任务的名称（唯一实例）；参数2：任务组的名称
                .build();

        // 3、触发器（Trigger）定义触发器，马上执行，然后每5秒重复执行一次
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("trigger1", "group1") // 参数1：触发器的名称（唯一实例）；参数2：触发器组的名称
                .startAt(startDate) // 设置任务的开始时间
                .build();
        // 4、让调度器关联任务和触发器，保证按照触发器定义的调整执行任务
        scheduler.scheduleJob(jobDetail, trigger);

        // 5、启动
        scheduler.start();
        // 关闭
        //scheduler.shutdown();
    }

}
----

案例二：或在指定的时间间隔内多次执行作业任务。

修改HelloSchedulerDemoSimpleTrigger.java
[source,java]
----
// 3、触发器（Trigger）定义触发器，马上执行，然后每5秒重复执行一次
Trigger trigger = TriggerBuilder.newTrigger()
        .withIdentity("trigger1", "group1") // 参数1：触发器的名称（唯一实例）；参数2：触发器组的名称
        .startAt(startDate) // 设置任务的开始时间
        .withSchedule(SimpleScheduleBuilder.simpleSchedule().repeatSecondlyForever(5).withRepeatCount(2))  // 每5秒执行一次，连续执行3次后停止，默认是0
        .build();
----

案例三：指定任务的结束时间。

修改HelloSchedulerDemoSimpleTrigger.java
[source,java]
----
// 设置任务的结束时间
Date endDate = new Date();
// 启动结束，任务在当前时间10秒后停止
endDate.setTime(endDate.getTime() +10000);

// 2、任务实例（JobDetail）定义一个任务调度实例，将该实例与HelloJobSimpleTrigger绑定，任务类需要实现Job接口
JobDetail jobDetail = JobBuilder.newJob(HelloJobSimpleTrigger.class) // 加载任务类，与HelloJob完成绑定，要求HelloJob实现Job接口
        .withIdentity("job1", "group1") // 参数1：任务的名称（唯一实例）；参数2：任务组的名称
        .build();

// 3、触发器（Trigger）定义触发器，马上执行，然后每5秒重复执行一次
Trigger trigger = TriggerBuilder.newTrigger()
        .withIdentity("trigger1", "group1") // 参数1：触发器的名称（唯一实例）；参数2：触发器组的名称
        .startAt(startDate) // 设置任务的开始时间
        .endAt(endDate) // 设置任务的结束时间
        .withSchedule(SimpleScheduleBuilder.simpleSchedule().repeatSecondlyForever(5).withRepeatCount(2))  // 每5秒执行一次，连续执行3次后停止，默认是0
        .build();
----

====
[CAUTION]
**需要注意的点**

* SimpleTrigger的属性有：开始时间、结束时间、重复次数和重复的时间间隔。
* 重复次数属性的值可以为0、正整数、或常量SimpleTrigger.REPEAT_INDEFINITELY。
* 重复的时间间隔属性值必须为大于0或者长整形的正整数，以毫秒作为时间单位，当重复的时间间隔为0时，意味着与Trigger同时触发执行。
* 如果有指定结束时间属性值，则结束时间属性优先于重复次数属性，这样的好处在于：当我们需要创建一个每间隔10秒触发一次直到指定的结束时间的Trigger，而无需去计算从开始到结束的所重复的次数，我们只需简单的指定结束时间和使用REPEAT_INDEFINITELY作为重复次数的属性值即可。
====

### 10、CronTrigger触发器
如果你需要像日历那样按日程来触发任务，而不是像SimpleTrigger那样每隔特定的间隔时间触发，CronTrigger通常比SimpleTrigger更有用，因为它是基于日历的作业调度器。

使用CronTrigger，你可以指定诸如“每个周五中午”，或者“每个工作日的9:30”或者“从每个周一、周三、周五的上午9:00到上午10:00之间每个五分钟”这样日程安排来触发。甚至，像SimpleTrigger一样，CronTrigger也有一个startTime以指定日程从什么时候开始，也有一个（可选的）endTime以指定何时日程不再继续。

（1）Cron Expressions——Cron表达式

Cron表达式被用来配置CronTrigger实例。Cron表达式是一个由7个子表达式组成的字符串。每个子表达式都描述了一个单独的日程细节。这些子表达式用空格分隔，分别表示：

. Seconds 秒
. Minutes 分钟
. Hours 小时
. Day-of-Month 月中的天
. Month 月
. Day-of-Week 周中的天
. Year（optional field）年（可选的域）

取值：

image::imgs/08.jpg[]

单个子表达式可以包含范围或者列表。例如：前面例子中的周中的天这个域（这里是“WED”）可以被替换为“MON-FRI”，“MON,WED,FRI”或者甚至“MON-WED,SAT”。

所有的域中的值都有特定的合法范围，这些值的合法范围相当明显，例如：秒和分域的合法值为0到59，小时的合法范围是0到23，Day-of-Month中值的合法范围是1到31，但是需要注意不同的月份中的天数不同。月份的合法值是1到12.或者用字符串JAN,FEB,MAR,APR,MAY,JUN,JUL,AUG,SEP,OCT,NOV及DEC来表示。Day-of-Week可以用1到7来表示（1=星期日）或者用字符串SUN,MON,TUE,WED,THU,FRI和SAT来表示。

image::imgs/09.jpg[]

练习一下：
[source,properties]
----
"0 0 10,14,16 * * ?" 每天上午10点，下午2点，4点
"0 0/30 9-17 * * ?" 朝九晚五工作时间内每半小时，从0分开始每隔30分钟发送一次
"0 0 12 ? * WED" 表示每个星期三中午12点
"0 0 12 * * ?" 每天中午12点触发
"0 15 10 ? * *" 每天上午10:15触发
"0 15 10 * * ?" 每天上午10:15触发
"0 15 10 * * ? *" 每天上午10:15触发
"0 15 10 * * ? 2005" 2005年的每天上午10:15触发
"0 * 14 * * ?" 在每天下午2点到下午2:59期间的每1分钟触发
"0 0/55 14 * * ?" 在每天下午2点到下午2:59期间，从0开始到55分钟触发
"0 0/55 14,18 * * ?" 在每天下午2点到下午2:59期间和下午6点到6:55期间，从0开始到55分钟触发
"0 0-5 14 * * ?" 在每天下午2点到下午2:05期间的每1分钟触发
"0 10,44 14 ? 3 WED" 每年3月的星期三的下午2:10和2:44触发
"0 15 10 ? * MON-FRI" 周一到周五的上午10:15触发
"0 15 10 15 * ?" 每月15日上午10:15触发
"0 15 10 L * ?" 每月最后一日的上午10:15触发
"0 15 10 ? * 6L" 每月的最后一个星期五上午10:15触发
"0 15 10 ? * 6L 2002-2005" 2002年至2005年的每月的最后一个星期五上午10:15触发
"0 15 10 ? * 6#3" 每月的第三个星期五上午10:15触发
----

案例：

HelloJobCronTrigger.java
[source,java]
----
// 定义任务类
public class HelloJobCronTrigger implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        // 输出当前时间
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = dateFormat.format(date);
        // 工作内容
        System.out.println("正在进行数据库的备份工作，备份数据库的时间是：" +dateString);
    }

}
----
HelloSchedulerDemoCronTrigger.java
[source,java]
----
public class HelloSchedulerDemoCronTrigger {

    public static void main(String[] args) throws Exception {
        // 1、调度器（Scheduler），从工厂中获取调度的实例（默认：实例化new StdSchedulerFactory();）
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

        // 2、任务实例（JobDetail）定义一个任务调度实例，将该实例与HelloJobSimpleTrigger绑定，任务类需要实现Job接口
        JobDetail jobDetail = JobBuilder.newJob(HelloJobCronTrigger.class) // 加载任务类，与HelloJob完成绑定，要求HelloJob实现Job接口
                .withIdentity("job1", "group1") // 参数1：任务的名称（唯一实例）；参数2：任务组的名称
                .build();

        // 3、触发器（Trigger）定义触发器，马上执行，然后每5秒重复执行一次
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("trigger1", "group1") // 参数1：触发器的名称（唯一实例）；参数2：触发器组的名称
                .withSchedule(CronScheduleBuilder.cronSchedule("0/5 * * * * ?"))  // 日历
                .build();
        // 4、让调度器关联任务和触发器，保证按照触发器定义的调整执行任务
        scheduler.scheduleJob(jobDetail, trigger);

        // 5、启动
        scheduler.start();
        // 关闭
        //scheduler.shutdown();
    }
}
----

====
[NOTE]
**小提示**

* “L”和“W”可以一起使用。（企业可用在工资计算）
* “#”可表示月中第几个周几。（企业可用在计算母亲节和父亲节）
* 周字段英文字母不区分大小写，例如MON=mon
* 利用工具，在线生成
====

### 11、配置、资源SchedulerFactory
Quartz以模块方式架构，因此，要使它运行，几个组件必须很好的咬合在一起。幸运的是，已经有了一些现存的助手可以完成这些工作。

所有的Scheduler实例由SchedulerFactory创建。

Quartz的三个核心概念：调度器、任务、触发器，三者之间的关系是：

image::imgs/10.jpg[]

大家都知道，一个作业，比较重要的三个要素就是Scheduler，JobDetail，Trigger；而Trigger对于Job而言就好比一个驱动器，没有触发器来定时驱动作业，作业就无法运行；对于Job而言，一个Job可以对应多个Trigger，但对于Trigger而言，一个Trigger只能对应一个Job，所以一个Trigger只能被指派给一个Job；如果你需要一个更负责的触发计划，你可以创建多个Trigger并指派它们给同一个Job。

Scheduler的创建方式：

（1）StdSchedulerFactory：

Quartz默认的SchedulerFactory

* 使用一组参数（java.util.Properties）来创建和初始化Quartz调度器
* 配置参数一般存储在quartz.properties文件中
* 调用getScheduler方法就能创建和初始化调度器对象

[source,java]
----
SchedulerFactory schedulerFactory = new StdSchedulerFactory();
Scheduler scheduler = schedulerFactory.getScheduler();
----

用法一：输出调度器开始的时间（重要：使得任务和触发器进行关联）：

Date schedulerjob(JobDetail jobDetail, Trigger trigger)
[source,java]
----
SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
System.out.println("调度器的开始时间是： " +dateFormat.format(schedulerjob(jobDetail, trigger)));
----

用法二：启动任务调度：

void start();
[source,java]
----
scheduler.start();
----

用法三：任务调度挂起，即暂停操作

void standby();
[source,java]
----
// Scheduler执行2秒后挂起
Thread.sleep(2000L);
scheduler.standby();
// Scheduler执行5秒后自动开启
Thread.sleep(5000L);
scheduler.start();
----

用法四：关闭任务调度

void shutdown();

shutdown(true)：表示等待所有正在执行的Job执行完毕之后，再关闭Scheduler +
shutdown(false)： 表示直接关闭Scheduler

测试一：
[source,java]
----
// Scheduler执行2秒后挂起
Thread.sleep(2000L);
scheduler.shutdown();
// Scheduler执行5秒后自动开启
Thread.sleep(5000L);
scheduler.start();
----

测试二：
[source,java]
----
// Scheduler执行2秒后挂起
Thread.sleep(2000L);
/**
 * shutdown(true)：表示等待所有正在执行的Job执行完毕之后，再关闭Scheduler
 * shutdown(false)： 表示直接关闭Scheduler
 */
scheduler.shutdown(false);
System.out.println("Scheduler是否被关闭：" +scheduler.isShutdown());
----
同时修改HelloJobScheduler.java +
任务调度延迟5秒执行
[source,java]
----
// 延迟任务执行的时间，推迟5秒后执行
try {
    Thread.sleep(5000L);
} catch (InterruptedException e) {
    // TODO Auto-generated catch block
    e.printStackTrace();
}
----

（2）DirectSchedulerFactory（了解）：

DirectSchedulerFactory是对SchedulerFactory的直接实现，通过它可以直接构建Scheduler、ThreadPool等
[source,java]
----
DirectSchedulerFactory directSchedulerFactory = DirectSchedulerFactory.getInstance();
Scheduler scheduler = directSchedulerFactory.getScheduler();
----

### 12、Quartz.properties
默认路径：quartz-2.3.0中的org.quartz中的quartz.properties

image::imgs/11.jpg[]

我们也可以在项目的资源下添加quartz.properties文件，去覆盖底层的配置文件。

*组成部分：*

* 调度器属性

org.quartz.scheduler.instanceName属性用来区分特定的调度器实例，可以按照功能用途来给调度器起名。

org.quartz.scheduler.instanceId属性和前者一样，也允许任何字符串，但这个值必须在所有调度器实例中是唯一的，尤其是在一个集群环境中，所为集群的唯一key。假如你想Quartz帮你生成这个值的话，可以设置为AUTO。

* 线程池设置

threadCount +
处理Job的线程个数，至少为1，但最多的话最好不要超过100，在多数机器上设置该值超过100的话就会显得相当不实用了，特别是在你的Job执行时间较长的情况下。

threadPriority +
线程的优先级，优先级别高的线程比级别低的线程优先得到执行。最小为1，最大为10，默认为5

org.quartz.threadPool.class +
一个实现了org.quartz.spi.threadPool接口的类，Quartz自带的线程池实现类是org.quartz.simpl.SimpleThreadPool

* 作业存储设置

描述了在调度器实例的生命周期中，Job和Trigger信息是如何被存储的。

* 插件配置

满足特定需求用到的Quartz插件的配置。

例子：
[source,properties]
----
#===============================================================
#Configure Main Scheduler Properties 调度器属性
#===============================================================
#调度器的实例名
org.quartz.scheduler.instanceName = QuartzScheduler
#调度器的实例ID，大多数情况设置为AUTO即可
org.quartz.scheduler.instanceId = AUTO

#===============================================================
#Configure ThreadPool 线程池属性
#===============================================================
#处理Job的线程个数，至少为1，但最多的话最好不要超过100，在多数机器上设置该值超过100的话显得相当不实用了，特别是在你的Job执行时间较长的情况下
org.quartz.threadPool.threadCount =  5
#线程的优先级，优先级别搞的线程比优先级别低的线程优先得到执行。最小为1，最大为10，默认为5
org.quartz.threadPool.threadPriority = 5
#一个实现了org.quartz.spi.threadPool接口的类，Quartz自带的线程池实现类是org.quartz.simpl.SimpleThreadPool
org.quartz.threadPool.class = org.quartz.simpl.SimpleThreadPool

#===============================================================
#Configure JobStore 作业存储设置
#===============================================================
#要使Job存储在内存中需要通过设置org.quartz.jobStore.class 属性为org.quartz.simpl.RAMJobStore
org.quartz.jobStore.class = org.quartz.simpl.RAMJobStore

#===============================================================
#Configure Plugins 插件配置
#===============================================================
org.quartz.plugin.jobInitializer.class = org.quartz.plugins.xml.JobInitializationPlugin

org.quartz.plugin.jobInitializer.overWriteExistingJobs = true
org.quartz.plugin.jobInitializer.failOnFileNotFound = true
org.quartz.plugin.jobInitializer.validating=false
----

也可以编写程序代码操作quartz.properties文件的内容：
[source,java]
----
public class QuartzProperties {

    public static void main(String[] args) {
        // 创建工厂实例
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
----

通过Properties设置工厂属性的缺点在于用硬编码，假如需要修改例子中线程数量，将不得不修改代码，然后重新编译。我们这里不推荐使用。

## 八、Quartz监听器
### 1、概念
Quartz的监听器用于当任务调度中你所关注事件发生时，能够及时获取这一事件的通知。类似于任务执行过程中的邮件、短信类的提醒。Quartz监听器主要由JobListener、TriggerListener、SchedulerListener三种，顾名思义，分布表示任务、触发器、调度器对应的监听器。三者的使用方法类似，在开始介绍三种监听器之前，需要明确两个概念：全局监听器与非全局监听器，二者的区别在于：

* 全局监听器能够接收到所有的Job/Trigger的事件通知
* 而非全局监听器只能接收到在其上注册的Job或者Trigger的事件，不在其上注册的Job或Trigger则不会进行监听。

本课程关于全局与非全局的监听器的使用，将一一介绍。

### 2、JobListener
任务调度过程中，与任务Job相关的事件包括：Job开始要执行的提示；Job执行完成的提示等。
[source,java]
----
public interface JobListener {
    public String getName();
    public void jobToBeExecuted(JobExecutionContext context);
    public void jobExecutionVetoed(JobExecutionContext context);
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException);
}
----

*其中：*

. getName方法：用于获取该JobListener的名称。
. jobToBeExecuted方法：Scheduler在JobDetail将要被执行时调用这个方法。
. jobExecutionVetoed方法：Scheduler在JobDetail即将被执行，但又被TriggerListener否决时会调用该方法。
. jobWasExecuted方法：Scheduler在JobDetail被执行之后调用这个方法。

示例：

HelloJobListener.java
[source,java]
----
// 定义任务类
public class HelloJobListener implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        // 输出当前时间
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = dateFormat.format(date);
        // 工作内容
        System.out.println("正在进行数据库的备份工作，备份数据库的时间是：" +dateString);
    }
}
----

创建自定义的JobListener

MyJobListener.java
[source,java]
----
public class MyJobListener implements JobListener {

    @Override
    public String getName() {
        String name = this.getClass().getSimpleName();
        System.out.println("监听器的名称是：" +name);
        return name;
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext context) {
        String name = context.getJobDetail().getKey().getName();
        System.out.println("Job的名称是：" +name + "          Scheduler在JobDetail将要被执行时调用的方法");
    }

    @Override
    public void jobExecutionVetoed(JobExecutionContext context) {
        String name = context.getJobDetail().getKey().getName();
        System.out.println("Job的名称是：" +name + "          Scheduler在JobDetail即将被执行，但又被TriggerListener否决时会调用该方法");
    }

    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        String name = context.getJobDetail().getKey().getName();
        System.out.println("Job的名称是：" +name + "          Scheduler在JobDetail被执行之后调用这个方法");
    }

}
----

执行调度器

HelloSchedulerDemoJobListener.java
[source,java]
----
public class HelloSchedulerDemoJobListener {

    public static void main(String[] args) throws Exception {
        // 1、调度器（Scheduler），从工厂中获取调度的实例（默认：实例化new StdSchedulerFactory();）
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

        // 2、任务实例（JobDetail）定义一个任务调度实例，将该实例与HelloJobSimpleTrigger绑定，任务类需要实现Job接口
        JobDetail jobDetail = JobBuilder.newJob(HelloJobListener.class) // 加载任务类，与HelloJob完成绑定，要求HelloJob实现Job接口
                .withIdentity("job1", "group1") // 参数1：任务的名称（唯一实例）；参数2：任务组的名称
                .build();

        // 3、触发器（Trigger）定义触发器，马上执行，然后每5秒重复执行一次
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("trigger1", "group1") // 参数1：触发器的名称（唯一实例）；参数2：触发器组的名称
                .startNow()
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().repeatSecondlyForever(5).withRepeatCount(2))  // 每5秒执行一次，连续执行3次后停止，默认是0
                .build();
        // 4、让调度器关联任务和触发器，保证按照触发器定义的调整执行任务
        scheduler.scheduleJob(jobDetail, trigger);
        
        // 创建并注册一个全局的Job Listener
        // scheduler.getListenerManager().addJobListener(new MyJobListener(), EverythingMatcher.allJobs());
        // 创建并注册一个局部的Job Listener，表示指定的任务Job
        scheduler.getListenerManager().addJobListener(new MyJobListener(), KeyMatcher.keyEquals(JobKey.jobKey("job1", "group1")));

        // 5、启动
        scheduler.start();
        // 关闭
        //scheduler.shutdown();
    }

}
----

### 3、TriggerListener
任务调度过程中，与触发器Trigger相关的事件包括：触发器触发、触发器未正确触发、触发器完成等。
[source,java]
----
public interface TriggerListener {
    public String getName();
    public void triggerFired(Trigger trigger, JobExecutionContext context);
    public boolean vetoJobExecution(Trigger trigger, JobExecutionContext context);
    public void triggerMisfired(Trigger trigger);
    public void triggerComplete(Trigger trigger, JobExecutionContext context,            CompletedExecutionInstruction triggerInstructionCode)
}
----
*其中：*

. getName方法：用于获取触发器的名称。
. triggerFired方法：当与监听器关联的Trigger被触发，Job上的Execute()方法将被执行时，Scheduler就调用该方法。
. vetoJobExecution方法：在Trigger触发后，Job将要执行时由Scheduler调用这个方法。TriggerListener给了一个选择去否决Job的执行。假如这个方法返回true，这个Job将不会为此次Trigger触发而得到执行。
. triggerMisfired方法：Scheduler调用这个方法是在Trigger错过触发时。你应该关注此方法中持续时间长的逻辑：在出现许多错过触发的Trigger时，长逻辑会导致骨牌效应。你应当保持这个方法尽量的小。
. triggerComplete方法：Trigger被触发并且完成了Job的执行时，Scheduler调用这个方法。

示例：

下面的例子简单展示了TriggerListener的使用，其中创建并注册TriggerListener与JobListener几乎类似。

HelloJobListener.java
[source,java]
----
// 定义任务类
public class HelloJobListener implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        // 输出当前时间
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = dateFormat.format(date);
        // 工作内容
        System.out.println("正在进行数据库的备份工作，备份数据库的时间是：" +dateString);
    }
}
----

MyTriggerListener.java
[source,java]
----
public class MyTriggerListener implements TriggerListener {
    
    private String name;
    // 构造方法，自定义传递触发器的名称，默认是类的名称
    public MyTriggerListener(String name) {
        super();
        this.name = name;
    }
    @Override
    public String getName() {
        return this.name;  // 不返还会抛出一个名称为空的异常
    }

//    @Override
//    public String getName() {
//        String name = this.getClass().getSimpleName();
//        System.out.println("触发器的名称：" +name);
//        return name;  // 不返还会抛出一个名称为空的异常
//    }

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
----

HelloSchedulerDemoTriggerListener.java
[source,java]
----
public class HelloSchedulerDemoTriggerListener {

    public static void main(String[] args) throws Exception {
        // 1、调度器（Scheduler），从工厂中获取调度的实例（默认：实例化new StdSchedulerFactory();）
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

        // 2、任务实例（JobDetail）定义一个任务调度实例，将该实例与HelloJobSimpleTrigger绑定，任务类需要实现Job接口
        JobDetail jobDetail = JobBuilder.newJob(HelloJobListener.class) // 加载任务类，与HelloJob完成绑定，要求HelloJob实现Job接口
                .withIdentity("job1", "group1") // 参数1：任务的名称（唯一实例）；参数2：任务组的名称
                .build();

        // 3、触发器（Trigger）定义触发器，马上执行，然后每5秒重复执行一次
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("trigger1", "group1") // 参数1：触发器的名称（唯一实例）；参数2：触发器组的名称
                .startNow()
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().repeatSecondlyForever(5).withRepeatCount(2))  // 每5秒执行一次，连续执行3次后停止，默认是0
                .build();
        // 4、让调度器关联任务和触发器，保证按照触发器定义的调整执行任务
        scheduler.scheduleJob(jobDetail, trigger);

        // 创建并注册一个全局的Trigger Listener
        // scheduler.getListenerManager().addTriggerListener(new MyTriggerListener(), EverythingMatcher.allTriggers());
        // 创建并注册一个局部的Trigger Listener
        scheduler.getListenerManager().addTriggerListener(new MyTriggerListener(), KeyMatcher.keyEquals(TriggerKey.triggerKey("trigger1", "group1")));

        // 5、启动
        scheduler.start();
        // 关闭
        //scheduler.shutdown();
    }

}
----


### 4、SchedulerListener
SchedulerListener会在Scheduler的生命周期中关键事件发生时被调用。与Scheduler有关的事件包括：增加一个Job/Trigger，删除一个Job/Trigger，Scheduler发生严重错误，关闭Scheduler等。
[source,java]
----
public interface SchedulerListener {
    public void jobScheduled(Trigger trigger);
    public void jobUnscheduled(TriggerKey triggerKey);
    public void triggerFinalized(Trigger trigger);
    public void triggersPaused(String triggerGroup);
    public void triggersResumed(String triggerGroup);
    public void jobsPaused(String jobGroup);
    public void jobsResumed(String jobGroup);
    public void schedulerError(String msg, SchedulerException cause);
    public void schedulerStarted();
    public void schedulerInStandbyMode();
    public void schedulerShutdown();
    public void schedulingDataCleared()
}
----
*其中：*

. jobScheduled方法：用于部署JobDetail时调用。
. jobUnscheduled方法：用于卸载JobDetail时调用。
. triggerFinalized方法：当一个Trigger来到了再也不会触发的状态时调用这个方法。除非这个Job已设置成了持久性，否则它就会从Scheduler中移除。
. triggersPaused方法：Scheduler调用这个方法是发生在一个Trigger或Trigger组被暂停时。假如是Trigger组的话，triggerName参数将为null。
. triggersResumed方法：Scheduler调用这个方法是发生在一个Trigger或Trigger组从暂停中恢复时。假如是Trigger组的话，triggerName参数将为null。
. jobsPaused方法：当一个或一组JobDetail暂停时调用这个方法。
. jobsResumed方法：当一个或一组Job从暂停上恢复时调用这个方法。假如是一个Job组，jobName将为null。
. schedulerError方法：在Scheduler的正常运行期间产生一个严重错误时调用这个方法。
. schedulerStarted方法：当Scheduler开启时，调用该方法。
. schedulerInStandbyMode方法：当Scheduler处于StandBy模式时，调用该方法。
. schedulerShutdown方法：当Scheduler停止时，调用该方法。
. schedulingDataCleared方法：当Scheduler中的数据被清除时，调用该方法。

示例：

下面的代码简单描述了如何使用SchedulerListener方法：

HelloJobListener.java
[source,java]
----
// 定义任务类
public class HelloJobListener implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        // 输出当前时间
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = dateFormat.format(date);
        // 工作内容
        System.out.println("正在进行数据库的备份工作，备份数据库的时间是：" +dateString);
    }
}
----

MySchedulerListener.java
[source,java]
----
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
----

HelloSchedulerDemoTriggerListener.java
[source,java]
----
public class HelloSchedulerDemoTriggerListener {

    public static void main(String[] args) throws Exception {
        // 1、调度器（Scheduler），从工厂中获取调度的实例（默认：实例化new StdSchedulerFactory();）
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

        // 2、任务实例（JobDetail）定义一个任务调度实例，将该实例与HelloJobSimpleTrigger绑定，任务类需要实现Job接口
        JobDetail jobDetail = JobBuilder.newJob(HelloJobListener.class) // 加载任务类，与HelloJob完成绑定，要求HelloJob实现Job接口
                .withIdentity("job1", "group1") // 参数1：任务的名称（唯一实例）；参数2：任务组的名称
                .build();

        // 3、触发器（Trigger）定义触发器，马上执行，然后每5秒重复执行一次
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("trigger1", "group1") // 参数1：触发器的名称（唯一实例）；参数2：触发器组的名称
                .startNow()
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().repeatSecondlyForever(5).withRepeatCount(2))  // 每5秒执行一次，连续执行3次后停止，默认是0
                .build();
        // 4、让调度器关联任务和触发器，保证按照触发器定义的调整执行任务
        scheduler.scheduleJob(jobDetail, trigger);

        // 创建调度器的监听
        scheduler.getListenerManager().addSchedulerListener(new MySchedulerListener());
        // 移除对应的调度器的监听
        // scheduler.getListenerManager().removeSchedulerListener(new MySchedulerListener());

        // 5、启动
        scheduler.start();

        // 线程延迟7秒后关闭
        Thread.sleep(7000L);

        // 关闭
        scheduler.shutdown();
    }

}
----

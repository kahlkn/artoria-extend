package artoria.util;//package artoria.extend.core.util;
//
//import org.junit.Test;
//import org.quartz.CronTrigger;
//import org.quartz.JobDetail;
//import org.quartz.Scheduler;
//import org.quartz.TriggerKey;
//import org.quartz.impl.JobDetailImpl;
//import org.quartz.impl.triggers.SimpleTriggerImpl;
//
//import java.util.Date;
//
//public class QuartzUtilsTest {
//
//    @Test
//    public void test0() throws Exception {
//        Scheduler scheduler = QuartzUtils.getScheduler();
//        QuartzUtils.startScheduler(scheduler);
//
//        JobDetailImpl jobDetail = QuartzUtils.createJobDetail();
//        jobDetail.setJobClass(TaskDemo.class);
//        jobDetail.setName("job1");
//
//        SimpleTriggerImpl trigger = QuartzUtils.createSimpleTrigger();
//        trigger.setName("trigger1");
//        trigger.setRepeatCount(10);
//        trigger.setRepeatInterval(1000);
//        trigger.setStartTime(new Date(new Date().getTime() + 1000));
//
//        QuartzUtils.scheduleJob(scheduler, jobDetail, trigger);
//        Thread.sleep(30000);
//        QuartzUtils.shutdownScheduler(scheduler);
//    }
//
//    @Test
//    public void test1() throws Exception {
//        Scheduler scheduler = QuartzUtils.getScheduler();
//        QuartzUtils.startScheduler(scheduler);
//        JobDetail job = QuartzUtils.createJobBuilder(TaskDemo.class).build();
//
//        SimpleTriggerImpl trigger = QuartzUtils.createSimpleTrigger();
//        trigger.setName("trigger1");
//        trigger.setRepeatCount(10);
//        trigger.setRepeatInterval(1000);
//        trigger.setStartTime(new Date(new Date().getTime() + 1000));
//
//        QuartzUtils.scheduleJob(scheduler, job, trigger);
//        Thread.sleep(30000);
//        QuartzUtils.shutdownScheduler(scheduler);
//    }
//
//    @Test
//    public void test2() throws Exception {
//        Scheduler scheduler = QuartzUtils.getScheduler();
//        QuartzUtils.startScheduler(scheduler);
//
//        JobDetail job = QuartzUtils.createJobBuilder(TaskDemo.class).build();
//        CronTrigger trigger = QuartzUtils.createCronTriggerBuilder("0/5 * * * * ?").build();
//
//        QuartzUtils.scheduleJob(scheduler, job, trigger);
//        Thread.sleep(30000);
//        QuartzUtils.shutdownScheduler(scheduler);
//    }
//
//    @Test
//    public void test3() throws Exception {
//        Scheduler scheduler = QuartzUtils.getScheduler();
//        QuartzUtils.startScheduler(scheduler);
//        TriggerKey triggerKey = new TriggerKey("trigger");
//
//        JobDetail job = QuartzUtils.createJobBuilder(TaskDemo.class).build();
//        CronTrigger trigger = QuartzUtils
//                .createCronTriggerBuilder("0/8 * * * * ?")
//                .withIdentity(triggerKey).build();
//
//        QuartzUtils.scheduleJob(scheduler, job, trigger);
//        Thread.sleep(10000);
//
//        CronTrigger newTrigger = QuartzUtils
//                .createCronTriggerBuilder("0/1 * * * * ?")
//                .withIdentity(triggerKey).build();
//        if (scheduler.checkExists(triggerKey)) {
//            scheduler.rescheduleJob(triggerKey, newTrigger);
//        }
//        Thread.sleep(30000);
//        QuartzUtils.shutdownScheduler(scheduler);
//    }
//
//    @Test
//    public void test4() throws Exception {
//        Scheduler scheduler = QuartzUtils.getScheduler();
//        QuartzUtils.startScheduler(scheduler);
//        TriggerKey triggerKey = QuartzUtils.createTriggerKey("trigger");
//
//        JobDetail job = QuartzUtils.createJobBuilder(TaskDemo.class).build();
//        CronTrigger trigger = QuartzUtils
//                .createCronTriggerBuilder("0/8 * * * * ?")
//                .withIdentity(triggerKey).build();
//
//        QuartzUtils.scheduleJob(scheduler, job, trigger);
//        Thread.sleep(10000);
//
//        CronTrigger newTrigger = QuartzUtils
//                .createCronTriggerBuilder("0/1 * * * * ?")
//                .withIdentity(triggerKey).build();
//        QuartzUtils.rescheduleJob(scheduler, triggerKey, newTrigger);
//
//        Thread.sleep(30000);
//        QuartzUtils.shutdownScheduler(scheduler);
//    }
//
//}

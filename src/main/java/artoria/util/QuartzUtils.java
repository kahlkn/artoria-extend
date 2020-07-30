package artoria.util;

import org.quartz.*;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.quartz.impl.triggers.SimpleTriggerImpl;

import java.util.Collection;
import java.util.Date;

@Deprecated
public class QuartzUtils {
    private static SchedulerFactory defaultSchedulerFactory = new StdSchedulerFactory();

    public static void setDefaultSchedulerFactory(SchedulerFactory schedulerFactory) {
        defaultSchedulerFactory = schedulerFactory;
    }

    public static SchedulerFactory createSchedulerFactory() {
        return new StdSchedulerFactory();
    }

    public static JobKey createJobKey(String name) {
        return new JobKey(name, null);
    }

    public static JobKey createJobKey(String name, String group) {
        return new JobKey(name, group);
    }

    public static TriggerKey createTriggerKey(String name) {
        return new TriggerKey(name, null);
    }

    public static TriggerKey createTriggerKey(String name, String group) {
        return new TriggerKey(name, group);
    }

    public static JobDetailImpl createJobDetail() {
        return new JobDetailImpl();
    }

    public static SimpleTriggerImpl createSimpleTrigger() {
        return new SimpleTriggerImpl();
    }

    public static CronTriggerImpl createCronTrigger() {
        return new CronTriggerImpl();
    }

    public static JobBuilder createJobBuilder() {
        return JobBuilder.newJob();
    }

    public static JobBuilder createJobBuilder(Class<? extends Job> jobClass) {
        return JobBuilder.newJob(jobClass);
    }

    public static TriggerBuilder<Trigger> createTriggerBuilder() {
        return TriggerBuilder.newTrigger();
    }

    public static TriggerBuilder<CronTrigger> createCronTriggerBuilder(String cronExpression) {
        return TriggerBuilder.newTrigger().withSchedule(CronScheduleBuilder.cronSchedule(cronExpression));
    }

    public static TriggerBuilder<CronTrigger> createCronTriggerBuilder(CronExpression cronExpression) {
        return TriggerBuilder.newTrigger().withSchedule(CronScheduleBuilder.cronSchedule(cronExpression));
    }

    public static Scheduler getScheduler() throws SchedulerException {
        return defaultSchedulerFactory.getScheduler();
    }

    public static Scheduler getScheduler(SchedulerFactory schedulerFactory) throws SchedulerException {
        return schedulerFactory.getScheduler();
    }

    public static Scheduler getScheduler(String schedName) throws SchedulerException {
        return defaultSchedulerFactory.getScheduler(schedName);
    }

    public static Scheduler getScheduler(SchedulerFactory schedulerFactory, String schedName) throws SchedulerException {
        return schedulerFactory.getScheduler(schedName);
    }

    public static Collection<Scheduler> getAllSchedulers() throws SchedulerException {
        return defaultSchedulerFactory.getAllSchedulers();
    }

    public static Collection<Scheduler> getAllSchedulers(SchedulerFactory schedulerFactory) throws SchedulerException {
        return schedulerFactory.getAllSchedulers();
    }

    public static Date scheduleJob(Scheduler scheduler, Trigger trigger) throws SchedulerException {
        return scheduler.scheduleJob(trigger);
    }

    public static Date scheduleJob(Scheduler scheduler, JobDetail jobDetail, Trigger trigger) throws SchedulerException {
        return scheduler.scheduleJob(jobDetail, trigger);
    }

    public static Date rescheduleJob(Scheduler scheduler, TriggerKey triggerKey, Trigger newTrigger) throws SchedulerException {
        return scheduler.rescheduleJob(triggerKey, newTrigger);
    }

    public static void startScheduler(Scheduler scheduler) throws SchedulerException {
        scheduler.start();
    }

    public static void shutdownScheduler(Scheduler scheduler) throws SchedulerException {
        scheduler.shutdown();
    }

    public static void shutdownScheduler(Scheduler scheduler, boolean waitForJobsToComplete) throws SchedulerException {
        scheduler.shutdown(waitForJobsToComplete);
    }

}

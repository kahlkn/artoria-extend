package artoria.schedule;

import artoria.logging.Logger;
import artoria.logging.LoggerFactory;
import artoria.util.Assert;

public class DelayTaskUtils {
    private static Logger log = LoggerFactory.getLogger(DelayTaskUtils.class);
    private static DelayTaskScheduler delayTaskScheduler;

    public static DelayTaskScheduler getDelayTaskScheduler() {

        return delayTaskScheduler;
    }

    public static void setDelayTaskScheduler(DelayTaskScheduler delayTaskScheduler) {
        Assert.notNull(delayTaskScheduler, "Parameter \"delayTaskScheduler\" must not null. ");
        log.info("Set delay task scheduler: {}", delayTaskScheduler.getClass().getName());
        DelayTaskUtils.delayTaskScheduler = delayTaskScheduler;
    }

    public static DelayTaskHandler unregister(String handlerClassName) {

        return getDelayTaskScheduler().unregister(handlerClassName);
    }

    public static void register(DelayTaskHandler delayTaskHandler) {

        getDelayTaskScheduler().register(delayTaskHandler);
    }

    public static DelayTask removeDelayTask(String taskId) {

        return getDelayTaskScheduler().removeDelayTask(taskId);
    }

    public static void addDelayTask(DelayTask delayTask) {

        getDelayTaskScheduler().addDelayTask(delayTask);
    }

}

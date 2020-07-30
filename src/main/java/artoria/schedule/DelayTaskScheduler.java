package artoria.schedule;

import artoria.lifecycle.Destroyable;
import artoria.lifecycle.Initializable;

public interface DelayTaskScheduler extends Initializable, Destroyable {

    DelayTaskHandler unregister(String handlerClassName);

    void register(DelayTaskHandler delayTaskHandler);

    DelayTask removeDelayTask(String taskId);

    void addDelayTask(DelayTask delayTask);

}

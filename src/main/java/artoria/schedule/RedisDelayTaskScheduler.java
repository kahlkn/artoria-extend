package artoria.schedule;

import artoria.exchange.JsonUtils;
import artoria.lifecycle.LifecycleException;
import artoria.logging.Logger;
import artoria.logging.LoggerFactory;
import artoria.thread.SimpleThreadFactory;
import artoria.util.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

import static artoria.common.Constants.*;
import static java.lang.Boolean.TRUE;
import static java.util.concurrent.TimeUnit.SECONDS;

public class RedisDelayTaskScheduler implements DelayTaskScheduler {
    private static final Map<String, DelayTaskHandler> HANDLER_MAP = new ConcurrentHashMap<String, DelayTaskHandler>();
    private static final String EXECUTOR_THREAD_NAME_PREFIX = "redis-delay-task-executor";
    private static final String SCANNER_THREAD_NAME_PREFIX = "redis-delay-task-scanner";
    private static final String DELAY_TASK_QUEUE_KEY_PREFIX = "SCHEDULE:DELAY_TASK:QUEUE:";
    private static final String DELAY_TASK_KEY_PREFIX = "SCHEDULE:DELAY_TASK:";
    private static final String DEFAULT_PAGE_SIZE = "10";
    private static Logger log = LoggerFactory.getLogger(RedisDelayTaskScheduler.class);
    private final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;
    private final ThreadPoolExecutor threadPoolExecutor;
    private StringRedisTemplate redisTemplate;
    private String queueName;
    private TimeUnit timeUnit;
    private Long period;

    public RedisDelayTaskScheduler(StringRedisTemplate redisTemplate,
                                   String queueName, Long period, TimeUnit timeUnit) {
        Assert.notNull(redisTemplate, "Parameter \"redisTemplate\" must not null. ");
        Assert.notBlank(queueName, "Parameter \"queueName\" must not blank. ");
        Assert.notNull(timeUnit, "Parameter \"timeUnit\" must not null. ");
        Assert.notNull(period, "Parameter \"period\" must not null. ");
        this.scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(
                ONE, new SimpleThreadFactory(SCANNER_THREAD_NAME_PREFIX, TRUE)
        );
        this.threadPoolExecutor = new ThreadPoolExecutor(
                FOUR, TEN, ONE, SECONDS, new LinkedBlockingQueue<Runnable>(),
                new SimpleThreadFactory(EXECUTOR_THREAD_NAME_PREFIX, TRUE)
        );
        this.redisTemplate = redisTemplate;
        this.queueName = queueName.toUpperCase();
        this.timeUnit = timeUnit;
        this.period = period;
        scheduledThreadPoolExecutor.scheduleAtFixedRate(
                new DelayTaskScanner(), period, period, timeUnit
        );
        ShutdownHookUtils.addExecutorService(threadPoolExecutor);
        ShutdownHookUtils.addExecutorService(scheduledThreadPoolExecutor);
    }

    @Override
    public void initialize() throws LifecycleException {

    }

    @Override
    public DelayTaskHandler unregister(String handlerClassName) {
        Assert.notBlank(handlerClassName, "Parameter \"handlerClassName\" must not blank. ");
        DelayTaskHandler remove = HANDLER_MAP.remove(handlerClassName);
        if (remove != null) {
            log.info("Unregister delay task handler \"{}\". ", handlerClassName);
        }
        return remove;
    }

    @Override
    public void register(DelayTaskHandler delayTaskHandler) {
        Assert.notNull(delayTaskHandler, "Parameter \"delayTaskHandler\" must not null. ");
        Class<? extends DelayTaskHandler> handlerClass = delayTaskHandler.getClass();
        String className = handlerClass.getName();
        log.info("Register delay task handler \"{}\". ", className);
        HANDLER_MAP.put(className, delayTaskHandler);
    }

    @Override
    public DelayTask removeDelayTask(String taskId) {
        Assert.notBlank(taskId, "Parameter \"taskId\" must not blank. ");
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        ZSetOperations<String, String> zSetOperations = redisTemplate.opsForZSet();
        String taskKey = DELAY_TASK_KEY_PREFIX + queueName + COLON + taskId;
        String queueKey = DELAY_TASK_QUEUE_KEY_PREFIX + queueName;
        zSetOperations.remove(queueKey, taskId);
        String jsonString = valueOperations.get(taskKey);
        if (StringUtils.isBlank(jsonString)) { return null; }
        redisTemplate.delete(taskKey);
        return JsonUtils.parseObject(jsonString, DelayTask.class);
    }

    @Override
    public void addDelayTask(DelayTask delayTask) {
        Assert.notNull(delayTask, "Parameter \"delayTask\" must not null. ");
        String taskId = delayTask.getId();
        Assert.notBlank(taskId, "Parameter \"taskId\" must not blank. ");
        Date deliveryTime = delayTask.getDeliveryTime();
        Long delayTime = delayTask.getDelayTime();
        TimeUnit timeUnit = delayTask.getTimeUnit();
        long score;
        if (deliveryTime != null) {
            score = deliveryTime.getTime();
        }
        else if (delayTime != null && timeUnit != null) {
            score = System.currentTimeMillis() + timeUnit.toMillis(delayTime);
        }
        else {
            throw new IllegalArgumentException("Delay time or delivery time is null");
        }
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        ZSetOperations<String, String> zSetOperations = redisTemplate.opsForZSet();
        String jsonString = JsonUtils.toJsonString(delayTask);
        String taskKey = DELAY_TASK_KEY_PREFIX + queueName + COLON + taskId;
        String queueKey = DELAY_TASK_QUEUE_KEY_PREFIX + queueName;
        valueOperations.set(taskKey, jsonString);
        zSetOperations.add(queueKey, taskId, score);
    }

    @Override
    public void destroy() throws Exception {
        if (scheduledThreadPoolExecutor != null
                && !scheduledThreadPoolExecutor.isShutdown()) {
            scheduledThreadPoolExecutor.shutdown();
            ShutdownHookUtils.removeExecutorService(scheduledThreadPoolExecutor);
        }
        if (threadPoolExecutor != null
                && !threadPoolExecutor.isShutdown()) {
            threadPoolExecutor.shutdown();
            ShutdownHookUtils.removeExecutorService(threadPoolExecutor);
        }
    }

    class DelayTaskRunnable implements Runnable {
        private DelayTaskHandler delayTaskHandler;
        private DelayTask delayTask;

        DelayTaskRunnable(DelayTaskHandler delayTaskHandler, DelayTask delayTask) {
            Assert.notNull(delayTaskHandler, "Parameter \"delayTaskHandler\" must not null. ");
            Assert.notNull(delayTask, "Parameter \"delayTask\" must not null. ");
            this.delayTaskHandler = delayTaskHandler;
            this.delayTask = delayTask;
        }

        @Override
        public void run() {
            try {
                delayTaskHandler.handle(delayTask);
            }
            catch (Exception e) {
                log.info("Delay task is: " + JsonUtils.toJsonString(delayTask));
                log.info(e.getMessage(), e);
            }
        }

    }

    class DelayTaskScanner implements Runnable {
        private final String REDIS_LUA;

        DelayTaskScanner() {
            REDIS_LUA =
                "local taskIds = redis.call('zRangeByScore', KEYS[1], ARGV[1], ARGV[2], 'LIMIT', '0', ARGV[3]); " +
                "if (next(taskIds) == nil) then return taskIds; end; " +
                "redis.call('zRem', KEYS[1], unpack(taskIds)); " +
                "return taskIds; ";
        }

        private void dispatch(List<String> taskIds) {
            if (CollectionUtils.isEmpty(taskIds)) { return; }
            ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
            for (String taskId : taskIds) {
                if (StringUtils.isBlank(taskId)) { continue; }
                String taskKey = DELAY_TASK_KEY_PREFIX + queueName + COLON + taskId;
                String jsonString = valueOperations.get(taskKey);
                if (StringUtils.isBlank(jsonString)) { continue; }
                redisTemplate.delete(taskKey);
                DelayTask delayTask = JsonUtils.parseObject(jsonString, DelayTask.class);
                if (delayTask == null) { continue; }
                String handler = delayTask.getHandler();
                DelayTaskHandler taskHandler = HANDLER_MAP.get(handler);
                if (taskHandler == null) {
                    log.info("Delay task no handler find. {}", JsonUtils.toJsonString(delayTask));
                    delayTask.setDelayTime(period);
                    delayTask.setTimeUnit(timeUnit);
                    delayTask.setDeliveryTime(null);
                    addDelayTask(delayTask);
                    continue;
                }
                threadPoolExecutor.execute(new DelayTaskRunnable(taskHandler, delayTask));
            }
        }

        @Override
        public void run() {
            try {
                for (; true; ) {
                    RedisScript<List> redisScript = new DefaultRedisScript<List>(REDIS_LUA, List.class);
                    List<String> keyList = new ArrayList<String>();
                    keyList.add(DELAY_TASK_QUEUE_KEY_PREFIX + queueName);
                    String maxScore = String.valueOf(System.currentTimeMillis());
                    String minScore = "-inf";
                    Object[] args = new Object[] { minScore, maxScore, DEFAULT_PAGE_SIZE };
                    List<String> taskIds = ObjectUtils.cast(redisTemplate.execute(redisScript, keyList, args));
                    if (CollectionUtils.isEmpty(taskIds)) { return; }
                    dispatch(taskIds);
                }
            }
            catch (Exception e) {
                log.info(e.getMessage(), e);
            }
        }

    }

}

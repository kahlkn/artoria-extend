package artoria.schedule;

import artoria.spring.ApplicationContextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Configuration
@ConditionalOnProperty(name = "artoria.schedule.delay.enabled", havingValue = "true")
@EnableConfigurationProperties({DelayTaskProperties.class})
public class DelayTaskAutoConfiguration {
    private static Logger log = LoggerFactory.getLogger(DelayTaskAutoConfiguration.class);

    @Autowired
    public DelayTaskAutoConfiguration(DelayTaskProperties taskProperties, StringRedisTemplate redisTemplate) {
        String queueName = taskProperties.getQueueName();
        TimeUnit timeUnit = taskProperties.getTimeUnit();
        Long period = taskProperties.getPeriod();
        DelayTaskScheduler delayTaskScheduler =
                new RedisDelayTaskScheduler(redisTemplate, queueName, period, timeUnit);
        DelayTaskUtils.setDelayTaskScheduler(delayTaskScheduler);

        ApplicationContext applicationContext = ApplicationContextUtils.getContext();
        Map<String, DelayTaskHandler> beansOfType = applicationContext.getBeansOfType(DelayTaskHandler.class);
        for (Map.Entry<String, DelayTaskHandler> entry : beansOfType.entrySet()) {
            DelayTaskHandler value = entry.getValue();
            DelayTaskUtils.register(value);
        }
    }

}

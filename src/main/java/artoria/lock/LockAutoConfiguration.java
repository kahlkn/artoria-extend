package artoria.lock;

import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({LockProperties.class})
// TODO 如果 enabled 为 true 但是没有 redission 该怎么办
@ConditionalOnProperty(name = "artoria.lock.enabled", havingValue = "true")
public class LockAutoConfiguration {
    private static Logger log = LoggerFactory.getLogger(LockAutoConfiguration.class);

    @Autowired
    public LockAutoConfiguration(LockProperties lockProperties, RedissonClient redisson) {
        if (redisson != null) {
            LockUtils.setLocker(new RedisReentrantLocker(redisson));
            log.info("The redis reentrant locker was initialized success. ");
        }
    }

}

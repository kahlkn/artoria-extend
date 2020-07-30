package artoria.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import static artoria.common.Constants.*;

@Configuration
@ConditionalOnClass(name = {"org.springframework.data.redis.core.RedisTemplate"})
@AutoConfigureAfter({RedisAutoConfiguration.class})
@AutoConfigureBefore({CacheAutoConfiguration.class})
public class RedisCacheAutoConfiguration {
    private static final String DEFAULT_REDIS_CACHE_NAME = "redis";
    private static Logger log = LoggerFactory.getLogger(RedisCacheAutoConfiguration.class);

    @Bean
    public Cache redisCache(RedisTemplate<String, Object> redisTemplate) {
        long timeToLive = EIGHT * SIXTY * ONE_THOUSAND;
        RedisCache redisCache = new RedisCache(redisTemplate, DEFAULT_REDIS_CACHE_NAME, timeToLive, ZERO);
        redisCache.setPrintLog(true);
        log.info("The redis cache was initialized success. ");
        CacheUtils.register(redisCache);
        return redisCache;
    }

}

package artoria.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
@ConditionalOnClass({RedisTemplate.class})
@AutoConfigureAfter({org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration .class})
public class RedisAutoConfiguration implements InitializingBean, DisposableBean {
    private static Logger log = LoggerFactory.getLogger(RedisAutoConfiguration.class);
    private final StringRedisTemplate stringRedisTemplate;

    @Autowired
    public RedisAutoConfiguration(StringRedisTemplate stringRedisTemplate) {

        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        StringRedisSerializer serializer = new StringRedisSerializer();
        stringRedisTemplate.setDefaultSerializer(serializer);
        stringRedisTemplate.setKeySerializer(serializer);
        stringRedisTemplate.setValueSerializer(serializer);
        stringRedisTemplate.setHashKeySerializer(serializer);
        stringRedisTemplate.setHashValueSerializer(serializer);
    }

    @Bean
    @ConditionalOnMissingBean(name = "stringObjRedisTemplate")
    public RedisTemplate<String, Object> stringObjRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<String, Object>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        StringRedisSerializer serializer = new StringRedisSerializer();
        redisTemplate.setHashKeySerializer(serializer);
        redisTemplate.setKeySerializer(serializer);
        return redisTemplate;
    }

    @Override
    public void destroy() throws Exception {
    }

}

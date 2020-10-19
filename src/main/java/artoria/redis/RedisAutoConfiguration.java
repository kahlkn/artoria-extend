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
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
@ConditionalOnClass({RedisTemplate.class})
@AutoConfigureAfter({org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration .class})
public class RedisAutoConfiguration implements InitializingBean, DisposableBean {
    private static Logger log = LoggerFactory.getLogger(RedisAutoConfiguration.class);
    private final RedisTemplate<String, String> stringRedisTemplate;

    @Autowired
    public RedisAutoConfiguration(StringRedisTemplate stringRedisTemplate) {

        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
//        StringRedisSerializer serializer = new StringRedisSerializer();
//        stringRedisTemplate.setDefaultSerializer(serializer);
//        stringRedisTemplate.setKeySerializer(serializer);
//        stringRedisTemplate.setValueSerializer(serializer);
//        stringRedisTemplate.setHashKeySerializer(serializer);
//        stringRedisTemplate.setHashValueSerializer(serializer);
    }

    /*@Bean
    @ConditionalOnMissingBean(name = "strStrRedisTemplate")
    public RedisTemplate<String, String> strStrRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        StringRedisSerializer strSerializer = new StringRedisSerializer();
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<String, String>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setDefaultSerializer(new JdkSerializationRedisSerializer());
        redisTemplate.setKeySerializer(strSerializer);
        redisTemplate.setValueSerializer(strSerializer);
        redisTemplate.setHashKeySerializer(strSerializer);
        redisTemplate.setHashValueSerializer(strSerializer);
        return redisTemplate;
    }*/

    @Bean
    @ConditionalOnMissingBean(name = "strObjRedisTemplate")
    public RedisTemplate<String, Object> strObjRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        JdkSerializationRedisSerializer jdkSerializer = new JdkSerializationRedisSerializer();
        RedisSerializer<String> strSerializer = new org.springframework.data.redis.serializer.StringRedisSerializer ();
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<String, Object>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setDefaultSerializer(jdkSerializer);
        redisTemplate.setKeySerializer(strSerializer);
        redisTemplate.setValueSerializer(jdkSerializer);
        redisTemplate.setHashKeySerializer(strSerializer);
        redisTemplate.setHashValueSerializer(jdkSerializer);
        return redisTemplate;
    }

    @Override
    public void destroy() throws Exception {
    }

}

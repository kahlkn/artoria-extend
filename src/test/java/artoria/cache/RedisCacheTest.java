package artoria.cache;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@Ignore
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {CacheAutoConfiguration.class, RedisAutoConfiguration.class})
@Import({CacheAutoConfiguration.class})
public class RedisCacheTest {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    public void test1() {
    }

}

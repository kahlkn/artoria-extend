package artoria.cache;

import com.alibaba.fastjson.JSON;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Ignore
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {CacheAutoConfiguration.class, RedisAutoConfiguration.class})
@Import({CacheAutoConfiguration.class})
public class RedisTemplateTest {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private RedisTemplate<Serializable, Object> serializableRedisTemplate;

    @Test
    public void test1() throws Exception {
        System.out.println(stringRedisTemplate);
        System.out.println(serializableRedisTemplate);
        ValueOperations<String, String> opsForValue = stringRedisTemplate.opsForValue();
        opsForValue.set("key1", "val1");
        System.out.println(opsForValue.get("key1"));
        ValueOperations<Serializable, Object> opsForValue1 = serializableRedisTemplate.opsForValue();
        opsForValue1.set("key2", "val2");
        System.out.println(opsForValue1.get("key2"));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void test2() throws Exception {
        ValueOperations<Serializable, Object> opsForValue1 = serializableRedisTemplate.opsForValue();
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("data - key - 1", "data - val - 1");
        data.put("data - key - 2", "data - val - 2");
        data.put("data - key - 3", "data - val - 3");
        data.put("data - key - 4", "data - val - 4");
        opsForValue1.set("test-json-map", JSON.toJSONString(data));
        Map<String, Object> map = JSON.parseObject((String) opsForValue1.get("test-json-map"), Map.class);
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            System.out.println(entry.getKey() + " | " + entry.getValue());
        }
    }

}

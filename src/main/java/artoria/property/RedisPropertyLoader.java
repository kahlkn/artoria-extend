package artoria.property;

import artoria.util.CollectionUtils;
import artoria.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static artoria.common.Constants.*;

public class RedisPropertyLoader implements PropertyLoader {
    private static Logger log = LoggerFactory.getLogger(RedisPropertyLoader.class);
    private StringRedisTemplate stringRedisTemplate;

    public RedisPropertyLoader(StringRedisTemplate stringRedisTemplate) {

        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public Map<String, Map<String, Object>> loadAll() {
        Map<String, Map<String, Object>> result = new HashMap<String, Map<String, Object>>(ONE_HUNDRED);
        HashOperations<String, Object, Object> opsForHash = stringRedisTemplate.opsForHash();
        Map<Object, Object> entries = opsForHash.entries("PROPERTY:NAME_VALUE");
        // TODO: Need to optimize.
        Set<String> keys = stringRedisTemplate.keys("PROPERTY:GROUP_NAME:" + ASTERISK);
        SetOperations<String, String> opsForSet = stringRedisTemplate.opsForSet();
        if (CollectionUtils.isNotEmpty(keys)) {
            for (String key : keys) {
                if (StringUtils.isBlank(key)) { continue; }
                Set<String> members = opsForSet.members(key);
                if (CollectionUtils.isEmpty(members)) { continue; }
                String group = key.substring("PROPERTY:GROUP_NAME:".length());
                Map<String, Object> groupMap = result.get(group);
                if (groupMap == null) {
                    groupMap = new HashMap<String, Object>(TWENTY);
                    result.put(group, groupMap);
                }
                for (String member : members) {
                    if (StringUtils.isBlank(member)) {
                        continue;
                    }
                    Object value = entries.get(member);
                    groupMap.put(member, value);
                }
            }
        }
        return result;
    }

}

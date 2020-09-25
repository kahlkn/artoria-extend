package artoria.property;

import artoria.logging.Logger;
import artoria.logging.LoggerFactory;
import artoria.util.*;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.*;

import static artoria.common.Constants.*;

public class RedisPropertyProvider extends AbstractPropertyProvider {
    private static final String GROUP_NAME_KEY_PREFIX = "PROPERTY:GROUP_NAME:";
    private static final String NAME_VALUE_KEY = "PROPERTY:NAME_VALUE";
    private static Logger log = LoggerFactory.getLogger(RedisPropertyProvider.class);
    private StringRedisTemplate stringRedisTemplate;

    public RedisPropertyProvider(StringRedisTemplate stringRedisTemplate) {
        Assert.notNull(stringRedisTemplate, "Parameter \"stringRedisTemplate\" must not null. ");
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public boolean containsProperty(String name) {
        Assert.notBlank(name, "Parameter \"name\" must not blank. ");
        HashOperations<String, Object, Object> opsForHash = stringRedisTemplate.opsForHash();
        return BooleanUtils.parseBoolean(opsForHash.hasKey(NAME_VALUE_KEY, name));
    }

    @Override
    public Object getProperty(String name, Object defaultValue) {
        if (StringUtils.isBlank(name)) { return null; }
        HashOperations<String, Object, Object> opsForHash = stringRedisTemplate.opsForHash();
        Object value = opsForHash.get(NAME_VALUE_KEY, name);
        return value != null ? value : defaultValue;
    }

    @Override
    public Object setProperty(String group, String name, Object value) {
        Assert.notBlank(group, "Parameter \"group\" must not blank. ");
        Assert.notBlank(name, "Parameter \"name\" must not blank. ");
        Assert.notNull(value, "Parameter \"value\" must not null. ");
        HashOperations<String, Object, Object> opsForHash = stringRedisTemplate.opsForHash();
        Object oldValue = opsForHash.get(NAME_VALUE_KEY, name);
        opsForHash.put(NAME_VALUE_KEY, name, value);
        SetOperations<String, String> opsForSet = stringRedisTemplate.opsForSet();
        String groupKey = GROUP_NAME_KEY_PREFIX + group;
        opsForSet.add(groupKey, name);
        return oldValue;
    }

    @Override
    public Object removeProperty(String name) {
        Assert.notBlank(name, "Parameter \"name\" must not blank. ");
        // TODO: Need to optimize.
        Set<String> keys = stringRedisTemplate.keys(GROUP_NAME_KEY_PREFIX + ASTERISK);
        SetOperations<String, String> opsForSet = stringRedisTemplate.opsForSet();
        if (CollectionUtils.isNotEmpty(keys)) {
            for (String key : keys) {
                Long remove = opsForSet.remove(key, name);
                if (remove != null && remove == ONE) { break; }
            }
        }
        HashOperations<String, Object, Object> opsForHash = stringRedisTemplate.opsForHash();
        Object oldValue = opsForHash.get(NAME_VALUE_KEY, name);
        opsForHash.delete(NAME_VALUE_KEY, name);
        return oldValue;
    }

    @Override
    public Map<String, Object> getProperties(String group) {
        if (StringUtils.isBlank(group)) {
            return Collections.emptyMap();
        }
        String groupKey = GROUP_NAME_KEY_PREFIX + group;
        SetOperations<String, String> opsForSet = stringRedisTemplate.opsForSet();
        Set<String> members = opsForSet.members(groupKey);
        if (CollectionUtils.isEmpty(members)) {
            return Collections.emptyMap();
        }
        HashOperations<String, Object, Object> opsForHash = stringRedisTemplate.opsForHash();
        Map<String, Object> result = new HashMap<String, Object>(members.size());
        for (String name : members) {
            Object value = opsForHash.get(NAME_VALUE_KEY, name);
            result.put(name, value);
        }
        return Collections.unmodifiableMap(result);
    }

    @Override
    public void reload(Object data) {
        Assert.notNull(data, "Parameter \"data\" must not null. ");
        Map<String, Map<String, Object>> dataMap = ObjectUtils.cast(data);
        Map<String, List<String>> groupNameListMap = new HashMap<String, List<String>>(dataMap.size());
        Map<String, Object> nameValueMap = new HashMap<String, Object>(ONE_HUNDRED);
        for (Map.Entry<String, Map<String, Object>> entry : dataMap.entrySet()) {
            Map<String, Object> properties = entry.getValue();
            String group = entry.getKey();
            if (MapUtils.isEmpty(properties)) { continue; }
            if (StringUtils.isBlank(group)) { continue; }
            List<String> list = groupNameListMap.get(group);
            if (list == null) {
                list = new ArrayList<String>();
                groupNameListMap.put(group, list);
            }
            list.addAll(properties.keySet());
            nameValueMap.putAll(properties);
        }
        HashOperations<String, Object, Object> opsForHash = stringRedisTemplate.opsForHash();
        Set<Object> oldKeys = opsForHash.keys(NAME_VALUE_KEY);
        List<Object> willDelete = new ArrayList<Object>(oldKeys);
        willDelete.removeAll(nameValueMap.keySet());
        if (MapUtils.isNotEmpty(nameValueMap)) {
            opsForHash.putAll(NAME_VALUE_KEY, nameValueMap);
        }
        if (CollectionUtils.isNotEmpty(willDelete)) {
            opsForHash.delete(NAME_VALUE_KEY, willDelete.toArray());
        }
        SetOperations<String, String> opsForSet = stringRedisTemplate.opsForSet();
        for (Map.Entry<String, List<String>> entry : groupNameListMap.entrySet()) {
            List<String> list = entry.getValue();
            String group = entry.getKey();
            if (list == null) { list = Collections.emptyList(); }
            if (StringUtils.isBlank(group)) { continue; }
            String groupKey = GROUP_NAME_KEY_PREFIX + group;
            Set<String> oldMembers = opsForSet.members(groupKey);
            if (oldMembers == null) { oldMembers = Collections.emptySet(); }
            willDelete = new ArrayList<Object>(oldMembers);
            willDelete.removeAll(list);
            String[] values = new String[list.size()];
            values = list.toArray(values);
            opsForSet.add(groupKey, values);
            if (CollectionUtils.isNotEmpty(willDelete)) {
                opsForSet.remove(groupKey, willDelete.toArray());
            }
        }
        if (PropertyUtils.getBooleanProperty(DISPLAY_RELOAD_LOG)) {
            log.info("The redis property provider reload data success. ");
        }
    }

}

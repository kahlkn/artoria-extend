package artoria.cache;

import artoria.exception.ExceptionUtils;
import artoria.util.Assert;
import artoria.util.CollectionUtils;
import artoria.util.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicLong;

import static artoria.common.Constants.*;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class RedisCache implements Cache {
    private static final String REDIS_KEY_PREFIX = "CACHE:";
    private static Logger log = LoggerFactory.getLogger(RedisCache.class);
    private final AtomicLong missCount = new AtomicLong();
    private final AtomicLong hitCount = new AtomicLong();
    private final RedisTemplate<String, Object> redisTemplate;
    private final String name;
    private final Long timeToIdle;
    private final Long timeToLive;
    private Boolean printLog;

    public RedisCache(RedisTemplate<String, Object> redisTemplate, String name) {

        this(redisTemplate, name, ZERO, ZERO);
    }

    public RedisCache(RedisTemplate<String, Object> redisTemplate, String name, long timeToLive, long timeToIdle) {
        Assert.notNull(redisTemplate, "Parameter \"redisTemplate\" must not null. ");
        Assert.notBlank(name, "Parameter \"name\" must not blank. ");
        timeToIdle = timeToIdle >= ZERO ? timeToIdle : ZERO;
        timeToLive = timeToLive >= ZERO ? timeToLive : ZERO;
        this.redisTemplate = redisTemplate;
        this.name = name;
        this.timeToLive = timeToLive;
        this.timeToIdle = timeToIdle;
        this.printLog = false;
    }

    private String redisKey(Object key) {

        return REDIS_KEY_PREFIX + name + COLON + key;
    }

    private void updateHitStatistic(boolean hit) {
        if (printLog) {
            log.info("The size of the cache named \"{}\" is \"{}\" " +
                            "and its hit counts are \"{}\" and its miss counts are \"{}\". "
                    , name, size(), hitCount, missCount);
        }
        (hit ? hitCount : missCount).incrementAndGet();
    }

    private void updateExpireTime(String redisKey, Long timeToLive, Long timeToIdle, boolean hit) {
        if (!hit) { return; }
        if (timeToLive == null) { timeToLive = this.timeToLive; }
        if (timeToIdle == null) { timeToIdle = this.timeToIdle; }
        if (timeToLive != null && timeToLive > ZERO) {
            redisTemplate.expire(redisKey, timeToLive, MILLISECONDS);
        }
        if (timeToIdle != null && timeToIdle > ZERO) {
            redisTemplate.expire(redisKey, timeToIdle, MILLISECONDS);
        }
    }

    public boolean getPrintLog() {

        return printLog;
    }

    public void setPrintLog(boolean printLog) {

        this.printLog = printLog;
    }

    @Override
    public String getName() {

        return name;
    }

    @Override
    public Object getNativeCache() {

        return redisTemplate;
    }

    @Override
    public int size() {
        String pattern = redisKey(ASTERISK);
        Set<String> keys = redisTemplate.keys(pattern);
        if (keys == null) { return ZERO; }
        return keys.size();
    }

    @Override
    public boolean containsKey(Object key) {
        Assert.notNull(key, "Parameter \"key\" must not null. ");
        String redisKey = redisKey(key);
        Boolean hasKey = redisTemplate.hasKey(redisKey);
        boolean result = Boolean.TRUE.equals(hasKey);
        updateExpireTime(redisKey, timeToLive, timeToIdle, result);
        updateHitStatistic(result);
        return result;
    }

    @Override
    public Object get(Object key) {

        return get(key, Object.class);
    }

    @Override
    public <T> T get(Object key, Callable<T> callable) {
        Assert.notNull(callable, "Parameter \"callable\" must not null. ");
        Object result = get(key, Object.class);
        if (result != null) {
            return ObjectUtils.cast(result);
        }
        try {
            Object call = callable.call();
            put(key, call);
            result = call;
        }
        catch (Exception e) {
            throw ExceptionUtils.wrap(e);
        }
        return ObjectUtils.cast(result);
    }

    @Override
    public <T> T get(Object key, Class<T> type) {
        Assert.notNull(type, "Parameter \"type\" must not null. ");
        Assert.notNull(key, "Parameter \"key\" must not null. ");
        ValueOperations<String, Object> opsForValue = redisTemplate.opsForValue();
        String redisKey = redisKey(key);
        Object valObj = opsForValue.get(redisKey);
        boolean hit = valObj != null;
        updateExpireTime(redisKey, timeToLive, timeToIdle, hit);
        updateHitStatistic(hit);
        return ObjectUtils.cast(valObj);
    }

    @Override
    public Object put(Object key, Object value) {

        return put(key, value, timeToLive, timeToIdle);
    }

    @Override
    public Object put(Object key, Object value, Long timeToLive, Long timeToIdle) {
        Assert.notNull(value, "Parameter \"value\" must not null. ");
        Assert.notNull(key, "Parameter \"key\" must not null. ");
        ValueOperations<String, Object> opsForValue = redisTemplate.opsForValue();
        String redisKey = redisKey(key);
        opsForValue.set(redisKey, value);
        updateExpireTime(redisKey, timeToLive, timeToIdle, Boolean.TRUE);
        return null;
    }

    @Override
    public Object putIfAbsent(Object key, Object value) {

        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<?, ?> map) {
        Assert.notEmpty(map, "Parameter \"map\" must not empty. ");
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public Object remove(Object key) {
        Assert.notNull(key, "Parameter \"key\" must not null. ");
        String redisKey = redisKey(key);
        redisTemplate.delete(redisKey);
        return null;
    }

    @Override
    public void removeAll(Collection<?> keys) {
        if (CollectionUtils.isEmpty(keys)) { return; }
        List<String> keyList = new ArrayList<String>();
        for (Object key : keys) {
            if (key == null) { continue; }
            keyList.add(String.valueOf(key));
        }
        redisTemplate.delete(keyList);
    }

    @Override
    public int prune() {

        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        String pattern = redisKey(ASTERISK);
        Set<String> keys = redisTemplate.keys(pattern);
        if (CollectionUtils.isEmpty(keys)) { return; }
        redisTemplate.delete(keys);
    }

    @Override
    public Collection<Object> keys() {

        throw new UnsupportedOperationException();
    }

    @Override
    public Map<Object, Object> entries() {

        throw new UnsupportedOperationException();
    }

    @Override
    public void destroy() throws Exception {

    }

}

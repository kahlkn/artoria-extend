package artoria.cache;

import artoria.util.Assert;
import artoria.util.MapUtils;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import static artoria.common.Constants.*;
import static java.util.Collections.emptyMap;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class CaffeineCache extends AbstractCache {
    private static Logger log = LoggerFactory.getLogger(CaffeineCache.class);
    private Cache<Object, ValueWrapper> storage;

    public CaffeineCache(String name, long capacity, long timeToLive, long timeToIdle) {
        super(name, false);
        boolean flag = capacity <= ZERO && timeToLive <= ZERO && timeToIdle <= ZERO;
        Assert.isFalse(flag,
        "A parameter must have a value in \"capacity\", \"timeToLive\", \"timeToIdle\". "
        );
        Caffeine<Object, Object> builder = Caffeine.newBuilder();
        if (timeToIdle > ZERO) { builder.expireAfterAccess(timeToIdle, MILLISECONDS); }
        if (timeToLive > ZERO) { builder.expireAfterWrite(timeToLive, MILLISECONDS); }
        if (capacity > ZERO) { builder.maximumSize(capacity); }
        builder.initialCapacity(FIFTY);
        this.storage = builder.build();
    }

    public CaffeineCache(String name, Caffeine<Object, Object> caffeineBuilder) {
        super(name, false);
        Assert.notNull(caffeineBuilder, "Parameter \"caffeineBuilder\" must not null. ");
        this.storage = caffeineBuilder.build();
    }

    @Override
    protected ValueWrapper getValueWrapper(Object key) {
        if (storage instanceof LoadingCache) {
            return ((LoadingCache<Object, ValueWrapper>) storage).get(key);
        }
        return storage.getIfPresent(key);
    }

    @Override
    protected ValueWrapper putValueWrapper(Object key, ValueWrapper valueWrapper) {
        storage.put(key, valueWrapper);
        return null;
    }

    @Override
    protected ValueWrapper removeValueWrapper(Object key) {
        storage.invalidate(key);
        return null;
    }

    @Override
    public Object getNativeCache() {

        return storage;
    }

    @Override
    public long size() {

        return storage.estimatedSize();
    }

    @Override
    public void clear() {

        storage.invalidateAll();
    }

    @Override
    public long prune() {
        long count = ZERO;
        ConcurrentMap<Object, ValueWrapper> asMap = storage.asMap();
        for (Map.Entry<Object, ValueWrapper> entry : asMap.entrySet()) {
            ValueWrapper valueWrapper = entry.getValue();
            Object key = entry.getKey();
            if (valueWrapper.isExpired()) {
                storage.invalidate(key);
                recordEviction(key, TWO);
                count++;
            }
        }
        storage.cleanUp();
        return count;
    }

    @Override
    public Map<Object, Object> entries() {
        ConcurrentMap<Object, ValueWrapper> asMap = storage.asMap();
        if (MapUtils.isEmpty(asMap)) { return emptyMap(); }
        Map<Object, Object> result = new HashMap<Object, Object>(asMap.size());
        for (Map.Entry<Object, ValueWrapper> entry : asMap.entrySet()) {
            ValueWrapper val = entry.getValue();
            Object key = entry.getKey();
            if (key == null || val == null) { continue; }
            if (val.isExpired()) {
                removeValueWrapper(key);
                recordEviction(key, TWO);
                continue;
            }
            result.put(key, val.getValue());
        }
        return Collections.unmodifiableMap(result);
    }

}

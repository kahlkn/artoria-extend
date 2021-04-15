package artoria.cache;

import org.springframework.cache.support.AbstractValueAdaptingCache;
import org.springframework.lang.Nullable;

import java.util.concurrent.Callable;

public class SpringCacheAdapter extends AbstractValueAdaptingCache {
    private Cache cache;

    public SpringCacheAdapter(Cache cache) {

        this(cache, Boolean.TRUE);
    }

    public SpringCacheAdapter(Cache cache, boolean allowNullValues) {
        super(allowNullValues);
        this.cache = cache;
    }

    @Override
    protected Object lookup(@Nullable Object key) {

        return cache.get(key);
    }

    @Nullable
    @Override
    public String getName() {

        return cache.getName();
    }

    @Nullable
    @Override
    public Object getNativeCache() {

        return cache;
    }

    @Override
    public <T> T get(@Nullable Object key, @Nullable Callable<T> valueLoader) {
        //fromStoreValue();toStoreValue();
        return cache.get(key, valueLoader);
    }

    @Override
    public void put(@Nullable Object key, Object value) {

        cache.put(key, value);
    }

    @Override
    public ValueWrapper putIfAbsent(@Nullable Object key, Object value) {
        Object oldValue = cache.putIfAbsent(key, value);
        return toValueWrapper(oldValue);
    }

    @Override
    public void evict(@Nullable Object key) {

        cache.remove(key);
    }

    @Override
    public void clear() {

        cache.clear();
    }

}

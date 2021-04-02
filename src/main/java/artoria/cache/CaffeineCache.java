package artoria.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.util.Map;

public class CaffeineCache extends AbstractCache {

    public CaffeineCache(String name) {
        super(name);
        Cache<Object, Object> cache = Caffeine.newBuilder().build();;
    }

    @Override
    protected ValueWrapper getValueWrapper(Object key) {
        return null;
    }

    @Override
    protected ValueWrapper putValueWrapper(Object key, ValueWrapper valueWrapper) {
        return null;
    }

    @Override
    protected ValueWrapper removeValueWrapper(Object key) {
        return null;
    }

    @Override
    public Object getNativeCache() {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public void clear() {

    }

    @Override
    public int prune() {
        return 0;
    }

    @Override
    public Map<Object, Object> entries() {
        return null;
    }
}

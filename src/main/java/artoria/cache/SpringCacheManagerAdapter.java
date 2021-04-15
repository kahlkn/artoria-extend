package artoria.cache;

import artoria.util.Assert;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.lang.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static artoria.common.Constants.TWENTY;

public class SpringCacheManagerAdapter implements CacheManager {
    private final Map<String, artoria.cache.Cache> storage;
    private final Map<String, Cache> cacheMap;
    private CacheFactory cacheFactory;

    public SpringCacheManagerAdapter(CacheFactory cacheFactory) {
        Assert.notNull(cacheFactory, "Parameter \"cacheFactory\" must not null. ");
        this.cacheFactory = cacheFactory;
        this.cacheMap = new ConcurrentHashMap<String, Cache>(TWENTY);
        this.storage = CacheUtils.getStorage();
    }

    protected Cache getMissingCache(String name) {
        artoria.cache.Cache cache = cacheFactory.getInstance(name);
        CacheUtils.register(cache);
        return new SpringCacheAdapter(cache);
    }

    @Override
    public Cache getCache(@Nullable String name) {
        Cache cache = cacheMap.get(name);
        if (cache != null) {
            return cache;
        }
        else {
            // Fully synchronize now for missing cache creation...
            synchronized (storage) {
                cache = cacheMap.get(name);
                if (cache != null) {
                    return cache;
                }
                //
                artoria.cache.Cache cache1 = storage.get(name);
                if (cache1 != null && !(cache1 instanceof UndefinedCache)) {
                    cache = new SpringCacheAdapter(cache1);
                    cacheMap.put(name, cache);
                    return cache;
                }
                cache = getMissingCache(name);
                if (cache != null) {
                    cacheMap.put(name, cache);
                }
                return cache;
            }
        }
    }

    @Nullable
    @Override
    public Collection<String> getCacheNames() {

        return Collections.unmodifiableSet(storage.keySet());
    }

}

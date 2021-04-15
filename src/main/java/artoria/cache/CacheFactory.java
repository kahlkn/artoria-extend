package artoria.cache;

import artoria.beans.BeanUtils;
import artoria.lang.ReferenceType;
import artoria.util.Assert;

import java.util.concurrent.TimeUnit;

public class CacheFactory {
    private CacheProperties cacheProperties;

    public CacheFactory(CacheProperties cacheProperties) {
        Assert.notNull(cacheProperties, "Parameter \"cacheProperties\" must not null. ");
        this.cacheProperties = cacheProperties;
    }

    public Cache getInstance(String cacheName) {
        Assert.notBlank(cacheName, "Parameter \"cacheName\" must not blank. ");
        CacheProperties.DefaultConfig defaultConfig = cacheProperties.getDefaultConfig();
        if (defaultConfig == null) {
            defaultConfig = new CacheProperties.DefaultConfig();
            defaultConfig.setType(CacheProperties.CacheType.SIMPLE);
            defaultConfig.setReferenceType(ReferenceType.SOFT);
        }
        CacheProperties.CacheConfig cacheConfig =
                BeanUtils.beanToBean(defaultConfig, CacheProperties.CacheConfig.class);
        cacheConfig.setName(cacheName);
        return getInstance(cacheConfig);
    }

    public Cache getInstance(CacheProperties.CacheConfig cacheConfig) {
        Assert.notNull(cacheConfig, "Parameter \"cacheConfig\" must not null. ");
        String cacheName = cacheConfig.getName();
        Assert.notBlank(cacheName, "Parameter \"cacheName\" must not blank. ");
        CacheProperties.CacheType type = cacheConfig.getType();
        Long capacity = cacheConfig.getCapacity();
        Long timeToLive = cacheConfig.getTimeToLive();
        TimeUnit timeToLiveUnit = cacheConfig.getTimeToLiveUnit();
        Long timeToIdle = cacheConfig.getTimeToIdle();
        TimeUnit timeToIdleUnit = cacheConfig.getTimeToIdleUnit();
        ReferenceType referenceType = cacheConfig.getReferenceType();
        Boolean printLog = cacheConfig.getPrintLog();
        if (printLog == null) { printLog = false; }
        switch (type) {
            case CAFFEINE:
            case SIMPLE: {
                if (timeToLiveUnit == null) { timeToLiveUnit = TimeUnit.MILLISECONDS; }
                if (timeToIdleUnit == null) { timeToIdleUnit = TimeUnit.MILLISECONDS; }
                if (capacity == null) { capacity = 0L; }
                if (timeToLive == null) { timeToLive = 0L; }
                else { timeToLive = timeToLiveUnit.toMillis(timeToLive); }
                if (timeToIdle == null) { timeToIdle = 0L; }
                else { timeToIdle = timeToIdleUnit.toMillis(timeToIdle); }
                if (referenceType == null) { referenceType = ReferenceType.SOFT; }
                Cache result;
                if (CacheProperties.CacheType.CAFFEINE.equals(type)) {
                    CaffeineCache cache = new CaffeineCache(cacheName, capacity, timeToLive, timeToIdle);
                    cache.setPrintLog(printLog);
                    result = cache;
                }
                else {
                    SimpleCache cache = new SpringSimpleCache(cacheName, capacity, timeToLive, timeToIdle, referenceType);
                    cache.setPrintLog(printLog);
                    result = cache;
                }
                return result;
            }
            case NONE:
            default: { return new NoCache(cacheName); }
        }
    }

}

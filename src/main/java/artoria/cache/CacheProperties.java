package artoria.cache;

import artoria.lang.ReferenceType;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.concurrent.TimeUnit;

@ConfigurationProperties("artoria.cache")
public class CacheProperties {
    private Boolean enabled;
    private Boolean springAdapter;
    private DefaultConfig defaultConfig;
    private List<CacheConfig> configs;

    public Boolean getEnabled() {

        return enabled;
    }

    public void setEnabled(Boolean enabled) {

        this.enabled = enabled;
    }

    public Boolean getSpringAdapter() {

        return springAdapter;
    }

    public void setSpringAdapter(Boolean springAdapter) {

        this.springAdapter = springAdapter;
    }

    public DefaultConfig getDefaultConfig() {

        return defaultConfig;
    }

    public void setDefaultConfig(DefaultConfig defaultConfig) {

        this.defaultConfig = defaultConfig;
    }

    public List<CacheConfig> getConfigs() {

        return configs;
    }

    public void setConfigs(List<CacheConfig> configs) {

        this.configs = configs;
    }

    public enum CacheType {

        /**
         * Caffeine backed caching.
         */
        CAFFEINE,

        /**
         * Simple in-memory caching.
         */
        SIMPLE,

        /**
         * No caching.
         */
        NONE

    }

    public static class DefaultConfig {
        private CacheType type = CacheType.NONE;
        private Long capacity;
        private Long timeToLive;
        private TimeUnit timeToLiveUnit = TimeUnit.MILLISECONDS;
        private Long timeToIdle;
        private TimeUnit timeToIdleUnit = TimeUnit.MILLISECONDS;
        private ReferenceType referenceType;
        private Boolean printLog;

        public CacheType getType() {
            return type;
        }

        public void setType(CacheType type) {
            this.type = type;
        }

        public Long getCapacity() {
            return capacity;
        }

        public void setCapacity(Long capacity) {
            this.capacity = capacity;
        }

        public Long getTimeToLive() {
            return timeToLive;
        }

        public void setTimeToLive(Long timeToLive) {
            this.timeToLive = timeToLive;
        }

        public TimeUnit getTimeToLiveUnit() {
            return timeToLiveUnit;
        }

        public void setTimeToLiveUnit(TimeUnit timeToLiveUnit) {
            this.timeToLiveUnit = timeToLiveUnit;
        }

        public Long getTimeToIdle() {
            return timeToIdle;
        }

        public void setTimeToIdle(Long timeToIdle) {
            this.timeToIdle = timeToIdle;
        }

        public TimeUnit getTimeToIdleUnit() {
            return timeToIdleUnit;
        }

        public void setTimeToIdleUnit(TimeUnit timeToIdleUnit) {
            this.timeToIdleUnit = timeToIdleUnit;
        }

        public ReferenceType getReferenceType() {
            return referenceType;
        }

        public void setReferenceType(ReferenceType referenceType) {
            this.referenceType = referenceType;
        }

        public Boolean getPrintLog() {
            return printLog;
        }

        public void setPrintLog(Boolean printLog) {
            this.printLog = printLog;
        }

    }

    public static class CacheConfig extends DefaultConfig {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

    }

}

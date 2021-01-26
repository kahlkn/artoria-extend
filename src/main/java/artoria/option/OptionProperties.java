package artoria.option;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.concurrent.TimeUnit;

/**
 * Option properties.
 * @author Kahle
 */
@ConfigurationProperties(prefix = "artoria.option")
public class OptionProperties {
    private ProviderType providerType = ProviderType.SIMPLE;
    private JdbcConfig jdbcConfig;
    private CustomConfig customConfig;

    public ProviderType getProviderType() {

        return providerType;
    }

    public void setProviderType(ProviderType providerType) {

        this.providerType = providerType;
    }

    public JdbcConfig getJdbcConfig() {

        return jdbcConfig;
    }

    public void setJdbcConfig(JdbcConfig jdbcConfig) {

        this.jdbcConfig = jdbcConfig;
    }

    public CustomConfig getCustomConfig() {

        return customConfig;
    }

    public void setCustomConfig(CustomConfig customConfig) {

        this.customConfig = customConfig;
    }

    public enum ProviderType {
        /**
         * Simple.
         */
        SIMPLE,
        /**
         * Jdbc.
         */
        JDBC,
        /**
         * Custom.
         */
        CUSTOM,
        ;
    }

    public static class CacheConfig {
        private String   cacheName;
        private Long     timeToLive;
        private TimeUnit timeUnit;

        public String getCacheName() {

            return cacheName;
        }

        public void setCacheName(String cacheName) {

            this.cacheName = cacheName;
        }

        public Long getTimeToLive() {

            return timeToLive;
        }

        public void setTimeToLive(Long timeToLive) {

            this.timeToLive = timeToLive;
        }

        public TimeUnit getTimeUnit() {

            return timeUnit;
        }

        public void setTimeUnit(TimeUnit timeUnit) {

            this.timeUnit = timeUnit;
        }

    }

    public static class JdbcConfig {
        private String ownerColumnName;
        private String nameColumnName;
        private String valueColumnName;
        private String tableName;
        private String whereContent;
        private CacheConfig cacheConfig;

        public String getOwnerColumnName() {

            return ownerColumnName;
        }

        public void setOwnerColumnName(String ownerColumnName) {

            this.ownerColumnName = ownerColumnName;
        }

        public String getNameColumnName() {

            return nameColumnName;
        }

        public void setNameColumnName(String nameColumnName) {

            this.nameColumnName = nameColumnName;
        }

        public String getValueColumnName() {

            return valueColumnName;
        }

        public void setValueColumnName(String valueColumnName) {

            this.valueColumnName = valueColumnName;
        }

        public String getTableName() {

            return tableName;
        }

        public void setTableName(String tableName) {

            this.tableName = tableName;
        }

        public String getWhereContent() {

            return whereContent;
        }

        public void setWhereContent(String whereContent) {

            this.whereContent = whereContent;
        }

        public CacheConfig getCacheConfig() {

            return cacheConfig;
        }

        public void setCacheConfig(CacheConfig cacheConfig) {

            this.cacheConfig = cacheConfig;
        }

    }

    public static class CustomConfig {
        private Class<? extends OptionProvider> springContextBeanType;
        private String springContextBeanName;

        public Class<? extends OptionProvider> getSpringContextBeanType() {

            return springContextBeanType;
        }

        public void setSpringContextBeanType(Class<? extends OptionProvider> springContextBeanType) {

            this.springContextBeanType = springContextBeanType;
        }

        public String getSpringContextBeanName() {

            return springContextBeanName;
        }

        public void setSpringContextBeanName(String springContextBeanName) {

            this.springContextBeanName = springContextBeanName;
        }

    }

}

package artoria.property;

import artoria.thread.SimpleThreadFactory;
import artoria.util.Assert;
import artoria.util.ShutdownHookUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import static artoria.common.Constants.ONE;
import static artoria.common.Constants.ZERO;
import static java.lang.Boolean.TRUE;

@Configuration
@EnableConfigurationProperties({PropertyProperties.class})
public class PropertyAutoConfiguration {
    private static final String THREAD_NAME_PREFIX = "property-provider-reload-executor";
    private static Logger log = LoggerFactory.getLogger(PropertyAutoConfiguration.class);
    private final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;

    @Autowired
    public PropertyAutoConfiguration(ApplicationContext appContext, PropertyProperties properties) {
        PropertyProvider propertyProvider = propertyProvider(appContext, properties);
        PropertyLoader propertyLoader = propertyLoader(appContext, properties);
        if (propertyProvider != null) { PropertyUtils.setPropertyProvider(propertyProvider); }
        PropertyProperties.ReloadType reloadType = properties.getReloadType();
        if (!PropertyProperties.ReloadType.NONE.equals(reloadType)) {
            TimeUnit reloadPeriodUnit = properties.getReloadPeriodUnit();
            Long reloadPeriod = properties.getReloadPeriod();
            ThreadFactory threadFactory = new SimpleThreadFactory(THREAD_NAME_PREFIX, TRUE);
            scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(ONE, threadFactory);
            Runnable runnable = new PropertyReloadRunnable(propertyProvider, propertyLoader);
            scheduledThreadPoolExecutor.scheduleAtFixedRate(runnable, ZERO, reloadPeriod, reloadPeriodUnit);
            ShutdownHookUtils.addExecutorService(scheduledThreadPoolExecutor);
        }
        else {
            scheduledThreadPoolExecutor = null;
        }
    }

    protected PropertyProvider propertyProvider(ApplicationContext context, PropertyProperties prop) {
        PropertyProvider propertyProvider;
        try {
            propertyProvider = context.getBean(PropertyProvider.class);
            return propertyProvider;
        }
        catch (Exception e) {
            log.debug("Failed to get \"propertyProvider\" from application context. ", e);
        }
        PropertyProperties.ProviderType providerType = prop.getProviderType();
        if (PropertyProperties.ProviderType.REDIS.equals(providerType)) {
            StringRedisTemplate redisTemplate = context.getBean(StringRedisTemplate.class);
            propertyProvider = new RedisPropertyProvider(redisTemplate);
        }
        else {
            propertyProvider = new SimplePropertyProvider();
        }
        return propertyProvider;
    }

    protected PropertyLoader propertyLoader(ApplicationContext context, PropertyProperties prop) {
        PropertyLoader propertyLoader;
        try {
            propertyLoader = context.getBean(PropertyLoader.class);
            return propertyLoader;
        }
        catch (Exception e) {
            log.debug("Failed to get \"propertyLoader\" from application context. ", e);
        }
        PropertyProperties.ReloadType reloadType = prop.getReloadType();
        if (PropertyProperties.ReloadType.JDBC.equals(reloadType)) {
            JdbcTemplate jdbcTemplate = context.getBean(JdbcTemplate.class);
            String groupColumnName = prop.getGroupColumnName();
            String nameColumnName = prop.getNameColumnName();
            String valueColumnName = prop.getValueColumnName();
            String tableName = prop.getTableName();
            String whereContent = prop.getWhereContent();
            propertyLoader = new JdbcPropertyLoader(jdbcTemplate, groupColumnName,
                    nameColumnName, valueColumnName, tableName, whereContent);
        }
        else if (PropertyProperties.ReloadType.REDIS.equals(reloadType)) {
            StringRedisTemplate redisTemplate = context.getBean(StringRedisTemplate.class);
            propertyLoader = new RedisPropertyLoader(redisTemplate);
        }
        else {
            propertyLoader = null;
        }
        return propertyLoader;
    }

    protected static class PropertyReloadRunnable implements Runnable {
        private static Logger log = LoggerFactory.getLogger(PropertyReloadRunnable.class);
        private PropertyProvider propertyProvider;
        private PropertyLoader propertyLoader;

        PropertyReloadRunnable(PropertyProvider propertyProvider, PropertyLoader propertyLoader) {
            Assert.notNull(propertyProvider, "Parameter \"propertyProvider\" must not null. ");
            Assert.notNull(propertyLoader, "Parameter \"propertyLoader\" must not null. ");
            this.propertyProvider = propertyProvider;
            this.propertyLoader = propertyLoader;
        }

        @Override
        public void run() {
            try {
                Map<String, Map<String, Object>> data = propertyLoader.loadAll();
                if (data != null) { propertyProvider.reload(data); }
            }
            catch (Exception e) {
                log.error("An error occurred while reloading the data. ", e);
            }
        }
        //
    }

}

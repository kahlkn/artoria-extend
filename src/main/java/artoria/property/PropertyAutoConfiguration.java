package artoria.property;

import artoria.thread.SimpleThreadFactory;
import artoria.util.CollectionUtils;
import artoria.util.ShutdownHookUtils;
import artoria.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import static artoria.common.Constants.*;
import static java.lang.Boolean.TRUE;

@Configuration
@EnableConfigurationProperties({PropertyProperties.class})
public class PropertyAutoConfiguration {
    private static final String THREAD_NAME_PREFIX = "property-provider-reload-executor";
    private static Logger log = LoggerFactory.getLogger(PropertyAutoConfiguration.class);
    private final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;
    private StringRedisTemplate stringRedisTemplate;
    private PropertyProperties propertyProperties;
    private PropertyProvider propertyProvider;
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public PropertyAutoConfiguration(PropertyProperties propertyProperties,
                                     JdbcTemplate jdbcTemplate,
                                     StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.propertyProperties = propertyProperties;
        this.jdbcTemplate = jdbcTemplate;
        //
        PropertyProperties.ProviderType providerType = propertyProperties.getProviderType();
        if (PropertyProperties.ProviderType.REDIS.equals(providerType)) {
            this.propertyProvider = new RedisPropertyProvider(stringRedisTemplate);
        }
        else {
            this.propertyProvider = new SimplePropertyProvider();
        }
        PropertyUtils.setPropertyProvider(propertyProvider);
        //
        PropertyProperties.ReloadType reloadType = propertyProperties.getReloadType();
        if (!PropertyProperties.ReloadType.NONE.equals(reloadType)) {
            TimeUnit reloadPeriodUnit = propertyProperties.getReloadPeriodUnit();
            Long reloadPeriod = propertyProperties.getReloadPeriod();
            ThreadFactory threadFactory = new SimpleThreadFactory(THREAD_NAME_PREFIX, TRUE);
            scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(ONE, threadFactory);
            Runnable runnable = new PropertyReloader(
                    propertyProvider, propertyProperties, jdbcTemplate, stringRedisTemplate
            );
            scheduledThreadPoolExecutor.scheduleAtFixedRate(runnable, ZERO, reloadPeriod, reloadPeriodUnit);
            ShutdownHookUtils.addExecutorService(scheduledThreadPoolExecutor);
        }
        else {
            scheduledThreadPoolExecutor = null;
        }
    }

    @Bean
    public PropertyProvider propertyProvider() {

        return propertyProvider;
    }

    public static class PropertyReloader implements Runnable {
        private static Logger log = LoggerFactory.getLogger(PropertyReloader.class);
        private StringRedisTemplate stringRedisTemplate;
        private PropertyProperties propertyProperties;
        private PropertyProvider propertyProvider;
        private JdbcTemplate jdbcTemplate;

        public PropertyReloader(PropertyProvider propertyProvider,
                                PropertyProperties propertyProperties,
                                JdbcTemplate jdbcTemplate,
                                StringRedisTemplate stringRedisTemplate) {
            this.stringRedisTemplate = stringRedisTemplate;
            this.propertyProperties = propertyProperties;
            this.propertyProvider = propertyProvider;
            this.jdbcTemplate = jdbcTemplate;
        }

        private Map<String, Map<String, Object>> loadByJdbc() {
            try {
                String groupColumnName = propertyProperties.getGroupColumnName();
                String nameColumnName = propertyProperties.getNameColumnName();
                String valueColumnName = propertyProperties.getValueColumnName();
                String whereContent = propertyProperties.getWhereContent();
                String tableName = propertyProperties.getTableName();
                String sql = String.format(
                        "select `%s` as 'group', `%s` as 'name', `%s` as 'value' from `%s` %s",
                        groupColumnName, nameColumnName, valueColumnName, tableName, whereContent
                );
                List<Map<String, Object>> mapList = jdbcTemplate.queryForList(sql);
                Map<String, Map<String, Object>> result =
                        new HashMap<String, Map<String, Object>>(ONE_HUNDRED);
                for (Map<String, Object> map : mapList) {
                    if (map == null) { continue; }
                    String group = (String) map.get("group");
                    String name = (String) map.get("name");
                    Object value = map.get("value");
                    if (StringUtils.isBlank(group)) { continue; }
                    if (StringUtils.isBlank(name)) { continue; }
                    Map<String, Object> groupMap = result.get(group);
                    if (groupMap == null) {
                        groupMap = new HashMap<String, Object>(TWENTY);
                        result.put(group, groupMap);
                    }
                    groupMap.put(name, value);
                }
                return result;
            }
            catch (Exception e) {
                log.error("An error occurred while executing the \"loadByJdbc\". ", e);
                return null;
            }
        }

        private Map<String, Map<String, Object>> loadByRedis() {
            try {
                Map<String, Map<String, Object>> result = new HashMap<String, Map<String, Object>>(ONE_HUNDRED);
                HashOperations<String, Object, Object> opsForHash = stringRedisTemplate.opsForHash();
                Map<Object, Object> entries = opsForHash.entries("PROPERTY:NAME_VALUE");
                // TODO: Need to optimize.
                Set<String> keys = stringRedisTemplate.keys("PROPERTY:GROUP_NAME:" + ASTERISK);
                SetOperations<String, String> opsForSet = stringRedisTemplate.opsForSet();
                if (CollectionUtils.isNotEmpty(keys)) {
                    for (String key : keys) {
                        if (StringUtils.isBlank(key)) { continue; }
                        Set<String> members = opsForSet.members(key);
                        if (CollectionUtils.isEmpty(members)) { continue; }
                        String group = key.substring("PROPERTY:GROUP_NAME:".length());
                        Map<String, Object> groupMap = result.get(group);
                        if (groupMap == null) {
                            groupMap = new HashMap<String, Object>(TWENTY);
                            result.put(group, groupMap);
                        }
                        for (String member : members) {
                            if (StringUtils.isBlank(member)) {
                                continue;
                            }
                            Object value = entries.get(member);
                            groupMap.put(member, value);
                        }
                    }
                }
                return result;
            }
            catch (Exception e) {
                log.error("An error occurred while executing the \"loadByRedis\". ", e);
                return null;
            }
        }

        @Override
        public void run() {
            PropertyProperties.ReloadType reloadType = propertyProperties.getReloadType();
            if (PropertyProperties.ReloadType.JDBC.equals(reloadType)) {
                Map<String, Map<String, Object>> map = loadByJdbc();
                if (map != null) { propertyProvider.reload(map); }
            }
            else if (PropertyProperties.ReloadType.REDIS.equals(reloadType)) {
                Map<String, Map<String, Object>> map = loadByRedis();
                if (map != null) { propertyProvider.reload(map); }
            }
            else {
            }//
        }

    }

}

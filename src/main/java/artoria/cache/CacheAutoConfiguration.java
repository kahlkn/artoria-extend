package artoria.cache;

import artoria.util.Assert;
import artoria.util.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConditionalOnProperty(name = "artoria.cache.enabled", havingValue = "true")
@EnableConfigurationProperties({CacheProperties.class})
public class CacheAutoConfiguration {
    private static Logger log = LoggerFactory.getLogger(CacheAutoConfiguration.class);
    private CacheFactory cacheFactory;

    public CacheAutoConfiguration(CacheProperties cacheProperties) {
        Assert.notNull(cacheProperties, "Parameter \"cacheProperties\" must not null. ");
        cacheFactory = new CacheFactory(cacheProperties);
        List<CacheProperties.CacheConfig> configs = cacheProperties.getConfigs();
        if (CollectionUtils.isNotEmpty(configs)) {
            for (CacheProperties.CacheConfig config : configs) {
                if (config == null) { continue; }
                Cache instance = cacheFactory.getInstance(config);
                if (instance != null) { CacheUtils.register(instance); }
            }
        }
    }

    @Bean
    public CacheFactory cacheFactory() {

        return cacheFactory;
    }

}

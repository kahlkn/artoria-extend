package artoria.cache;

import artoria.util.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@Configuration
@AutoConfigureAfter({CacheAutoConfiguration.class})
@ConditionalOnProperty(name = {"artoria.cache.enabled", "artoria.cache.spring-adapter"}, havingValue = "true")
@ConditionalOnMissingBean(org.springframework.cache.CacheManager.class)
@EnableConfigurationProperties({CacheProperties.class})
public class SpringCacheAdapterAutoConfiguration {
    private static Logger log = LoggerFactory.getLogger(SpringCacheAdapterAutoConfiguration.class);

    @Resource
    private CacheFactory cacheFactory;

    @Bean
    public CacheManager cacheManager() {
        Assert.notNull(cacheFactory, "Parameter \"cacheFactory\" must not null. ");
        return new SpringCacheManagerAdapter(cacheFactory);
    }

}

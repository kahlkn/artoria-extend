package artoria.cache;

import artoria.lang.ReferenceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

import static artoria.common.Constants.*;

@Configuration
public class CacheAutoConfiguration {
    private static Logger log = LoggerFactory.getLogger(CacheAutoConfiguration.class);

    public CacheAutoConfiguration() {
        SimpleCache simpleCache = new SpringSimpleCache(DEFAULT,
                ZERO, TimeUnit.HOURS.toMillis(TWO), ZERO, ReferenceType.SOFT);
        simpleCache.setPrintLog(true);
        CacheUtils.register(simpleCache);
    }

    @Bean
    public Cache simpleCache() {

        return CacheUtils.getCache(DEFAULT);
    }

}

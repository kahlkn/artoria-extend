package artoria.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static artoria.common.Constants.DEFAULT;

@Configuration
public class CacheAutoConfiguration {
    private static Logger log = LoggerFactory.getLogger(CacheAutoConfiguration.class);

    @Bean
    public Cache simpleCache() {

        return CacheUtils.getCache(DEFAULT);
    }

}

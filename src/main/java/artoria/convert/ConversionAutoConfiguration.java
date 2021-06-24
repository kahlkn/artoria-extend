package artoria.convert;

import artoria.cache.CacheUtils;
import artoria.cache.SimpleCache;
import artoria.lang.ReferenceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

import static artoria.common.Constants.ZERO;

@Configuration
//@ConditionalOnProperty(name = "artoria.cache.enabled", havingValue = "true")
//@EnableConfigurationProperties({CacheProperties.class})
public class ConversionAutoConfiguration {
    private static Logger log = LoggerFactory.getLogger(ConversionAutoConfiguration.class);

    public ConversionAutoConfiguration() {
        log.info("Start handling the conversion service provider's cache...");
        String cacheName="cache-conversion-provider-cache";
        SimpleCache simpleCache = new SimpleCache(
                cacheName, ZERO, 2 * 60 * 60 * 1000, ReferenceType.SOFT);
        simpleCache.setPrintLog(true);
        CacheUtils.register(simpleCache);
        ConversionProvider provider = ConversionUtils.getConversionProvider();
        provider = new CacheConversionProvider(provider, cacheName, 2L, TimeUnit.HOURS);
        ConversionUtils.setConversionProvider(provider);
        log.info(">> The conversion service provider increased cache successfully. ");
    }

}

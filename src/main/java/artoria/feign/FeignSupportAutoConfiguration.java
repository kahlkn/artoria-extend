package artoria.feign;

import feign.RequestInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Feign support auto configuration.
 * @author Kahle
 */
@Configuration
@ConditionalOnClass({RequestInterceptor.class})
public class FeignSupportAutoConfiguration {
    private static Logger log = LoggerFactory.getLogger(FeignSupportAutoConfiguration.class);

    @Bean
    public RequestInterceptor requestInterceptor(){
        RequestInterceptor requestInterceptor = new FeignRequestTransferInterceptor();
        log.info("The feign request transfer interceptor was initialized success. ");
        return requestInterceptor;
    }

}

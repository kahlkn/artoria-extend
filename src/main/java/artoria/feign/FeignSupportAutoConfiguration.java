package artoria.feign;

import feign.RequestInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Feign support auto configuration.
 * @author Kahle
 */
@Configuration
public class FeignSupportAutoConfiguration {
    private static Logger log = LoggerFactory.getLogger(FeignSupportAutoConfiguration.class);

    @Bean
    public RequestInterceptor requestInterceptor(){
        RequestInterceptor requestInterceptor = new FeignRequestHeaderTransferInterceptor();
        log.info("The feign request header transfer interceptor was initialized success. ");
        return requestInterceptor;
    }

}

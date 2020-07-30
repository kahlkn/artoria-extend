package artoria.logging;

import artoria.exception.ExceptionAutoConfiguration;
import artoria.util.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Access log auto configuration.
 * @author Kahle
 */
@Configuration
@AutoConfigureBefore({ExceptionAutoConfiguration.class})
@ConditionalOnProperty(name = "artoria.logging.print-access-log", havingValue = "true")
@EnableConfigurationProperties({LoggingProperties.class})
public class AccessLogAutoConfiguration implements WebMvcConfigurer {
    private static Logger log = LoggerFactory.getLogger(AccessLogAutoConfiguration.class);
    private LoggingProperties loggingProperties;

    @Autowired
    public AccessLogAutoConfiguration(LoggingProperties loggingProperties) {
        Assert.notNull(loggingProperties, "Parameter \"loggingProperties\" must not null. ");
        this.loggingProperties = loggingProperties;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        String[] wantExclude = loggingProperties.getExcludeAccessLogPathPatterns();
        List<String> willExclude = new ArrayList<String>(Arrays.asList(wantExclude));
        registry.addInterceptor(new AccessLogInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns(willExclude);
        log.info("The access log print tools was initialized success. ");
    }

}

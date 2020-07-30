package artoria.cors;

import artoria.util.Assert;
import artoria.util.CollectionUtils;
import artoria.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

import static artoria.common.Constants.ZERO;

/**
 * Cors filter auto configuration.
 * @author Kahle
 */
@Configuration
@ConditionalOnClass({CorsFilter.class})
@ConditionalOnProperty(name = "artoria.cors.enabled", havingValue = "true")
@EnableConfigurationProperties({CorsProperties.class})
public class CorsFilterAutoConfiguration {
    private static Logger log = LoggerFactory.getLogger(CorsFilterAutoConfiguration.class);
    private final CorsProperties corsProperties;

    @Autowired
    public CorsFilterAutoConfiguration(CorsProperties corsProperties) {
        Assert.notNull(corsProperties, "Parameter \"corsProperties\" must not null. ");
        this.corsProperties = corsProperties;
    }

    @Bean
    public CorsFilter corsFilter() {
        List<String> allowedOrigins = corsProperties.getAllowedOrigins();
        List<String> allowedHeaders = corsProperties.getAllowedHeaders();
        List<String> exposedHeaders = corsProperties.getExposedHeaders();
        List<String> allowedMethods = corsProperties.getAllowedMethods();
        Boolean allowCredentials = corsProperties.getAllowCredentials();
        Long maxAge = corsProperties.getMaxAge();
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        if (CollectionUtils.isNotEmpty(allowedOrigins)) {
            corsConfiguration.setAllowedOrigins(allowedOrigins);
        }
        else {
            log.warn("Cors is enabled but the allowed origins is not configured. ");
        }
        if (CollectionUtils.isNotEmpty(allowedHeaders)) {
            corsConfiguration.setAllowedHeaders(allowedHeaders);
        }
        if (CollectionUtils.isNotEmpty(exposedHeaders)) {
            corsConfiguration.setExposedHeaders(exposedHeaders);
        }
        if (CollectionUtils.isNotEmpty(allowedMethods)) {
            corsConfiguration.setAllowedMethods(allowedMethods);
        }
        if (allowCredentials != null) {
            corsConfiguration.setAllowCredentials(allowCredentials);
        }
        if (maxAge != null && maxAge > ZERO) {
            corsConfiguration.setMaxAge(maxAge);
        }
        List<String> urlPatterns = corsProperties.getUrlPatterns();
        Assert.notEmpty(urlPatterns, "Parameter \"pathPattern\" must not empty. ");
        UrlBasedCorsConfigurationSource configurationSource = new UrlBasedCorsConfigurationSource();
        for (String urlPattern : urlPatterns) {
            if (StringUtils.isBlank(urlPattern)) { continue; }
            configurationSource.registerCorsConfiguration(urlPattern, corsConfiguration);
        }
        CorsFilter corsFilter = new CorsFilter(configurationSource);
        log.info("The cors filter was initialized success. ");
        return corsFilter;
    }

}

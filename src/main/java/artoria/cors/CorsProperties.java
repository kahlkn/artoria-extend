package artoria.cors;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * Cross-origin resource sharing (CORS) properties.
 * @author Kahle
 */
@ConfigurationProperties("artoria.cors")
public class CorsProperties {
    private Boolean enabled;
    private List<CorsConfig> configs;

    public Boolean getEnabled() {

        return enabled;
    }

    public void setEnabled(Boolean enabled) {

        this.enabled = enabled;
    }

    public List<CorsConfig> getConfigs() {

        return configs;
    }

    public void setConfigs(List<CorsConfig> configs) {

        this.configs = configs;
    }

    public static class CorsConfig {
        private String urlPattern;
        private List<String> allowedOrigins;
        private List<String> allowedHeaders;
        private List<String> exposedHeaders;
        private List<String> allowedMethods;
        private Boolean allowCredentials;
        private Long maxAge;

        public String getUrlPattern() {

            return urlPattern;
        }

        public void setUrlPattern(String urlPattern) {

            this.urlPattern = urlPattern;
        }

        public List<String> getAllowedOrigins() {

            return allowedOrigins;
        }

        public void setAllowedOrigins(List<String> allowedOrigins) {

            this.allowedOrigins = allowedOrigins;
        }

        public List<String> getAllowedHeaders() {

            return allowedHeaders;
        }

        public void setAllowedHeaders(List<String> allowedHeaders) {

            this.allowedHeaders = allowedHeaders;
        }

        public List<String> getExposedHeaders() {

            return exposedHeaders;
        }

        public void setExposedHeaders(List<String> exposedHeaders) {

            this.exposedHeaders = exposedHeaders;
        }

        public List<String> getAllowedMethods() {

            return allowedMethods;
        }

        public void setAllowedMethods(List<String> allowedMethods) {

            this.allowedMethods = allowedMethods;
        }

        public Boolean getAllowCredentials() {

            return allowCredentials;
        }

        public void setAllowCredentials(Boolean allowCredentials) {

            this.allowCredentials = allowCredentials;
        }

        public Long getMaxAge() {

            return maxAge;
        }

        public void setMaxAge(Long maxAge) {

            this.maxAge = maxAge;
        }

    }

}

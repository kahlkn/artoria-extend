package artoria.logging;

import org.springframework.boot.context.properties.ConfigurationProperties;

import static artoria.common.Constants.ZERO;

/**
 * Logger properties.
 * @author Kahle
 */
@ConfigurationProperties(prefix = "artoria.logging")
public class LoggingProperties {
    /**
     * Print access log.
     */
    private Boolean printAccessLog;
    /**
     * URL patterns to which the registered interceptor should not apply to.
     */
    private String[] excludeAccessLogPathPatterns = new String[ZERO];

    public Boolean getPrintAccessLog() {

        return printAccessLog;
    }

    public void setPrintAccessLog(Boolean printAccessLog) {

        this.printAccessLog = printAccessLog;
    }

    public String[] getExcludeAccessLogPathPatterns() {

        return excludeAccessLogPathPatterns;
    }

    public void setExcludeAccessLogPathPatterns(String[] excludeAccessLogPathPatterns) {

        this.excludeAccessLogPathPatterns = excludeAccessLogPathPatterns;
    }

}

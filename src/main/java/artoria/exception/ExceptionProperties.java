package artoria.exception;

import artoria.util.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Exception properties.
 * @author Kahle
 */
@ConfigurationProperties(prefix = "artoria.exception")
public class ExceptionProperties {
    /**
     * Enabled default exception handler.
     */
    private Boolean enabled;
    /**
     * Enabled internal error page.
     */
    private Boolean internalErrorPage = true;
    /**
     * Error display page base template path.
     */
    private String baseTemplatePath = "/error";
    /**
     * Default error message.
     */
    private String defaultErrorMessage = "Internal server error. ";

    public Boolean getEnabled() {

        return enabled;
    }

    public void setEnabled(Boolean enabled) {

        this.enabled = enabled;
    }

    public Boolean getInternalErrorPage() {

        return internalErrorPage;
    }

    public void setInternalErrorPage(Boolean internalErrorPage) {
        if (internalErrorPage == null) { return; }
        this.internalErrorPage = internalErrorPage;
    }

    public String getBaseTemplatePath() {

        return baseTemplatePath;
    }

    public void setBaseTemplatePath(String baseTemplatePath) {
        if (StringUtils.isBlank(baseTemplatePath)) { return; }
        this.baseTemplatePath = baseTemplatePath;
    }

    public String getDefaultErrorMessage() {

        return defaultErrorMessage;
    }

    public void setDefaultErrorMessage(String defaultErrorMessage) {
        if (defaultErrorMessage == null) { return; }
        this.defaultErrorMessage = defaultErrorMessage;
    }

}

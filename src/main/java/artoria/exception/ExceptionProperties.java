package artoria.exception;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

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
    /**
     * Exception message configuration.
     */
    private List<ExceptionMessage> messages;

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

        this.internalErrorPage = internalErrorPage;
    }

    public String getBaseTemplatePath() {

        return baseTemplatePath;
    }

    public void setBaseTemplatePath(String baseTemplatePath) {

        this.baseTemplatePath = baseTemplatePath;
    }

    public String getDefaultErrorMessage() {

        return defaultErrorMessage;
    }

    public void setDefaultErrorMessage(String defaultErrorMessage) {

        this.defaultErrorMessage = defaultErrorMessage;
    }

    public List<ExceptionMessage> getMessages() {

        return messages;
    }

    public void setMessages(List<ExceptionMessage> messages) {

        this.messages = messages;
    }

    public static class ExceptionMessage {
        private Class<Exception>[] classes;
        private String errorMessage;
        private String errorCode;

        public Class<Exception>[] getClasses() {

            return classes;
        }

        public void setClasses(Class<Exception>[] classes) {

            this.classes = classes;
        }

        public String getErrorMessage() {

            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {

            this.errorMessage = errorMessage;
        }

        public String getErrorCode() {

            return errorCode;
        }

        public void setErrorCode(String errorCode) {

            this.errorCode = errorCode;
        }

    }

}

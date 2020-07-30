package artoria.exception;

import artoria.common.ErrorCode;

/**
 * Business exception.
 * @author Kahle
 */
public class BusinessException extends UncheckedException {
    private String description;
    private String code;

    private static String buildMessage(String code, String description) {
        description += " (Error Code: " + code + ")";
        return description;
    }

    private static String buildMessage(ErrorCode errorCode) {
        if (errorCode == null) {
            return buildMessage(null, null);
        }
        String description = errorCode.getDescription();
        String code = errorCode.getCode();
        return buildMessage(code, description);
    }

    public BusinessException() {

        super();
    }

    public BusinessException(String message) {

        super(message);
    }

    public BusinessException(Throwable cause) {

        super(cause);
    }

    public BusinessException(String message, Throwable cause) {

        super(message, cause);
    }

    public BusinessException(String code, String description) {
        super(buildMessage(code, description));
        this.description = description;
        this.code = code;
    }

    public BusinessException(String code, String description, Throwable cause) {
        super(buildMessage(code, description), cause);
        this.description = description;
        this.code = code;
    }

    public BusinessException(ErrorCode errorCode) {
        super(buildMessage(errorCode));
        if (errorCode != null) {
            this.description = errorCode.getDescription();
            this.code = errorCode.getCode();
        }
    }

    public BusinessException(ErrorCode errorCode, Throwable cause) {
        super(buildMessage(errorCode), cause);
        if (errorCode != null) {
            this.description = errorCode.getDescription();
            this.code = errorCode.getCode();
        }
    }

    public String getCode() {

        return code;
    }

    public String getDescription() {

        return description;
    }

}

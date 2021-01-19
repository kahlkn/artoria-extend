package artoria.exception;

import artoria.common.ErrorCode;

/**
 * Business exception.
 * @author Kahle
 */
public class BusinessException extends UncheckedException {
    private String description;
    private String code;

    public BusinessException() {

        super();
    }

    public BusinessException(String message) {

        this(null, message);
    }

    public BusinessException(Throwable cause) {

        super(cause);
    }

    public BusinessException(String message, Throwable cause) {

        this(null, message, cause);
    }

    public BusinessException(String code, String description) {
        super(description + " (Error Code: " + code + ")");
        this.description = description;
        this.code = code;
    }

    public BusinessException(String code, String description, Throwable cause) {
        super(description + " (Error Code: " + code + ")", cause);
        this.description = description;
        this.code = code;
    }

    public BusinessException(ErrorCode errorCode) {
        super(errorCode != null ? errorCode.getDescription() +
                " (Error Code: " + errorCode.getCode() + ")" : "null (Error Code: null)");
        if (errorCode != null) {
            this.description = errorCode.getDescription();
            this.code = errorCode.getCode();
        }
    }

    public BusinessException(ErrorCode errorCode, Throwable cause) {
        super(errorCode != null ? errorCode.getDescription() +
                " (Error Code: " + errorCode.getCode() + ")" : "null (Error Code: null)", cause);
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

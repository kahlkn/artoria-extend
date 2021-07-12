package artoria.exception;

import artoria.util.StringUtils;

/**
 * Business exception.
 * @author Kahle
 */
public class BusinessException extends UncheckedException {
    private static final String EMPTY_MESSAGE = "null (Error Code: null)";
    private String description;
    private String code;

    protected static String build(String code, String description) {
        boolean descNotBlank = StringUtils.isNotBlank(description);
        boolean codeNotBlank = StringUtils.isNotBlank(code);
        if (codeNotBlank && descNotBlank) {
            return description + " (Error Code: " + code + ")";
        }
        else if (descNotBlank) {
            return description + " (Error Code: " + code + ")";
        }
        else if (codeNotBlank) {
            return "An error has occurred and the error code is \"" + code + "\"";
        }
        else { return EMPTY_MESSAGE; }
    }

    protected static String build(ErrorCode errorCode) {
        if (errorCode == null) { return EMPTY_MESSAGE; }
        return build(errorCode.getCode(), errorCode.getDescription());
    }

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
        super(build(code, description));
        this.description = description;
        this.code = code;
    }

    public BusinessException(String code, String description, Throwable cause) {
        super(build(code, description), cause);
        this.description = description;
        this.code = code;
    }

    public BusinessException(ErrorCode errorCode) {
        super(build(errorCode));
        if (errorCode != null) {
            this.description = errorCode.getDescription();
            this.code = errorCode.getCode();
        }
    }

    public BusinessException(ErrorCode errorCode, Throwable cause) {
        super(build(errorCode), cause);
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

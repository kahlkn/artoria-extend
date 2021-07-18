package artoria.exception;

/**
 * Business exception.
 * @author Kahle
 */
public class BusinessException extends UncheckedException {
    private Object[] arguments;
    private String description;
    private String code;

    public BusinessException() {

        super();
    }

    public BusinessException(String message, Object... arguments) {

        this(null, message, arguments);
    }

    public BusinessException(Throwable cause) {

        super(cause);
    }

    public BusinessException(String message, Throwable cause, Object... arguments) {

        this(null, message, cause, arguments);
    }

    public BusinessException(String code, String description, Object... arguments) {
        super(ErrorCodeUtils.toString(code, description, arguments));
        this.arguments = arguments;
        this.description = description;
        this.code = code;
    }

    public BusinessException(String code, String description, Throwable cause, Object... arguments) {
        super(ErrorCodeUtils.toString(code, description), cause);
        this.arguments = arguments;
        this.description = description;
        this.code = code;
    }

    public BusinessException(ErrorCode errorCode, Object... arguments) {
        super(ErrorCodeUtils.toString(errorCode, arguments));
        if (errorCode != null) {
            this.arguments = arguments;
            this.description = errorCode.getDescription();
            this.code = errorCode.getCode();
        }
    }

    public BusinessException(ErrorCode errorCode, Throwable cause, Object... arguments) {
        super(ErrorCodeUtils.toString(errorCode, arguments), cause);
        if (errorCode != null) {
            this.arguments = arguments;
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

    public Object[] getArguments() {

        return arguments;
    }

}

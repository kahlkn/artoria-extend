package artoria.exception;

import artoria.common.ErrorCode;

public enum SystemErrorCode implements ErrorCode {
    USERNAME_IS_REQUIRED("SYS001", "Username is required. "),
    PASSWORD_IS_REQUIRED("SYS002", "Password is required. "),
    ;

    private String code;
    private String description;
    private String message;

    SystemErrorCode(String code, String description) {
        this.code = code;
        this.description = description;
    }

    SystemErrorCode(String code, String description, String message) {
        this.code = code;
        this.description = description;
        this.message = message;
    }

    @Override
    public String getCode() {

        return code;
    }

    @Override
    public String getDescription() {

        return description;
    }

    @Override
    public String getMessage() {

        return message;
    }

}

package artoria.common;

public class SimpleErrorCode implements ErrorCode {
    private String description;
    private String code;

    public SimpleErrorCode() {
    }

    public SimpleErrorCode(String code, String description) {
        this.description = description;
        this.code = code;
    }

    @Override
    public String getCode() {

        return code;
    }

    public void setCode(String code) {

        this.code = code;
    }

    @Override
    public String getDescription() {

        return description;
    }

    public void setDescription(String description) {

        this.description = description;
    }

}

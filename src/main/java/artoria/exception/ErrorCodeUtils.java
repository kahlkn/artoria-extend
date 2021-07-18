package artoria.exception;

import artoria.engine.template.PlainTemplateEngine;
import artoria.util.StringUtils;

public class ErrorCodeUtils {
    private static final String EMPTY_MESSAGE = "null (Error Code: null)";
    private static PlainTemplateEngine templateEngine;

    public static PlainTemplateEngine getTemplateEngine() {

        return templateEngine;
    }

    public static void setTemplateEngine(PlainTemplateEngine templateEngine) {

        ErrorCodeUtils.templateEngine = templateEngine;
    }

    public static String toString(String code, String description, Object... arguments) {
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

    public static String toString(ErrorCode errorCode, Object... arguments) {
        if (errorCode == null) { return EMPTY_MESSAGE; }
        String description = errorCode.getDescription();
        String code = errorCode.getCode();
        return toString(code, description, arguments);
    }

}

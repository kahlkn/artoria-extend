package artoria.exception;

import artoria.common.Errors;
import artoria.common.Result;
import artoria.common.SimpleErrorCode;
import artoria.util.Assert;
import artoria.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static artoria.common.Constants.*;
import static java.lang.Boolean.FALSE;

/**
 * Simple error handler.
 * @author Kahle
 */
public class SimpleErrorHandler implements ErrorHandler {
    private static Logger log = LoggerFactory.getLogger(SimpleErrorHandler.class);
    private static final String DEFAULT_ERROR_MESSAGE = "Internal server error. ";
    private static final String TEXT_HTML = "text/html";
    private final ErrorCodeProvider errorCodeProvider;
    private final Boolean internalErrorPage;
    private final String baseTemplatePath;

    public SimpleErrorHandler(ErrorCodeProvider errorCodeProvider,
                              Boolean internalErrorPage,
                              String baseTemplatePath) {
        Assert.notNull(errorCodeProvider, "Parameter \"errorCodeProvider\" must not null. ");
        Assert.notNull(internalErrorPage, "Parameter \"internalErrorPage\" must not null. ");
        Assert.notBlank(baseTemplatePath, "Parameter \"baseTemplatePath\" must not blank. ");
        this.errorCodeProvider = errorCodeProvider;
        this.internalErrorPage = internalErrorPage;
        this.baseTemplatePath = baseTemplatePath;
    }

    protected ErrorCode getErrorCode(HttpServletRequest request, HttpServletResponse response, Throwable throwable) {
        if (throwable == null) {
            int respStatus = response.getStatus();
            String description = "An error has occurred. (Response Status: " + respStatus + ") ";
            return new SimpleErrorCode(EMPTY_STRING, description);
        }
        if (!(throwable instanceof BusinessException)) {
            return Errors.INTERNAL_SERVER_ERROR;
        }
        // Way 1, set code and description build message.
        // Way 2, set message and description, no code or message is code.
        BusinessException bizException = (BusinessException) throwable;
        String description = bizException.getDescription();
        String code = bizException.getCode();
        ErrorCode errorCode = errorCodeProvider.getInstance(code);
        if (errorCode != null) { return errorCode; }
        String message = bizException.getMessage();
        errorCode = errorCodeProvider.getInstance(message);
        if (errorCode != null) { return errorCode; }
        if (StringUtils.isBlank(description)) {
            description = message;
        }
        if (StringUtils.isBlank(description)) {
            description = DEFAULT_ERROR_MESSAGE;
        }
        return new SimpleErrorCode(code, description);
    }

    protected Object pageResult(HttpServletRequest request, HttpServletResponse response, Throwable throwable) {
        ErrorCode errorCode = getErrorCode(request, response, throwable);
        int responseStatus = response.getStatus();
        if (!internalErrorPage) {
            String viewPath = baseTemplatePath + SLASH + responseStatus;
            ModelAndView modelAndView = new ModelAndView(viewPath);
            modelAndView.addObject("responseStatus", responseStatus);
            modelAndView.addObject("errorCode", errorCode.getCode());
            modelAndView.addObject("errorMessage", errorCode.getDescription());
            return modelAndView;
        }
        response.setContentType(TEXT_HTML + "; charset=" + DEFAULT_ENCODING_NAME);
        String html =
        "<!DOCTYPE HTML>\n" +
        "<html>\n" +
        "<head>\n" +
        "    <title>An error has occurred. </title>\n" +
        "</head>\n" +
        "<body>\n" +
        "    <h3>\n" +
        "        An error has occurred. \n" +
        "    </h3>\n" +
        "    Response Status: " + responseStatus + "<br />\n" +
        "    Error Code: " + errorCode.getCode() + "<br />\n" +
        "    Error Message: " + errorCode.getDescription() + "<br />\n" +
        "    Please check the log for details if necessary. <br />\n" +
        "    Powered by <a href=\"https://github.com/kahlkn/artoria\" target=\"_blank\">Artoria</a>. <br />\n" +
        "</body>\n" +
        "</html>\n";
        try { response.getWriter().write(html); }
        catch (IOException e) {
            throw ExceptionUtils.wrap(e);
        }
        return null;
    }

    @Override
    public Object handle(HttpServletRequest request, HttpServletResponse response, Throwable throwable) {
        String accept = request.getHeader("Accept");
        accept = StringUtils.isNotBlank(accept) ? accept.toLowerCase() : null;
        if (accept != null && accept.contains(TEXT_HTML)) {
            return pageResult(request, response, throwable);
        }
        ErrorCode errorCode = getErrorCode(request, response, throwable);
        return new Result<Object>(FALSE, errorCode.getCode(), errorCode.getDescription());
    }

}

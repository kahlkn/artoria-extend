package artoria.exception;

import artoria.common.Result;
import artoria.util.Assert;
import artoria.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import static artoria.common.Constants.DEFAULT_ENCODING_NAME;
import static artoria.common.Constants.SLASH;

/**
 * Simple error handler.
 * @author Kahle
 */
public class SimpleErrorHandler implements ErrorHandler {
    private static Logger log = LoggerFactory.getLogger(SimpleErrorHandler.class);
    private static final String TEXT_HTML = "text/html";
    private Boolean internalErrorPage;
    private String baseTemplatePath;
    private String defaultErrorMessage;

    public SimpleErrorHandler(Boolean internalErrorPage, String baseTemplatePath, String defaultErrorMessage) {
        Assert.notBlank(defaultErrorMessage, "Parameter \"defaultErrorMessage\" must not blank. ");
        Assert.notBlank(baseTemplatePath, "Parameter \"baseTemplatePath\" must not blank. ");
        Assert.notNull(internalErrorPage, "Parameter \"internalErrorPage\" must not null. ");
        this.defaultErrorMessage = defaultErrorMessage;
        this.baseTemplatePath = baseTemplatePath;
        this.internalErrorPage = internalErrorPage;
    }

    protected Object createPageResult(HttpServletRequest request, HttpServletResponse response, Throwable throwable) {
        String errorMessage = null;
        String errorCode = null;
        if (throwable instanceof BusinessException) {
            BusinessException businessEx = (BusinessException) throwable;
            errorMessage = businessEx.getDescription();
            errorCode = businessEx.getCode();
        }
        if (StringUtils.isBlank(errorMessage)) {
            errorMessage = defaultErrorMessage;
        }
        int responseStatus = response.getStatus();
        if (internalErrorPage) {
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
            "    Error Code: " + errorCode + "<br />\n" +
            "    Error Message: " + errorMessage + "<br />\n" +
            "    Please check the log for details if necessary. <br />\n" +
            "    Powered by <a href=\"https://github.com/kahlkn/artoria\" target=\"_blank\">Artoria</a>. <br />\n" +
            "</body>\n" +
            "</html>\n";
            try {
                response.setContentType(TEXT_HTML + ";charset=" + DEFAULT_ENCODING_NAME);
                PrintWriter writer = response.getWriter();
                writer.write(html);
            }
            catch (IOException e) {
                throw ExceptionUtils.wrap(e);
            }
            return null;
        }
        else {
            String viewPath = baseTemplatePath + SLASH + responseStatus;
            ModelAndView modelAndView = new ModelAndView(viewPath);
            if (StringUtils.isNotBlank(errorMessage)) {
                modelAndView.addObject("responseStatus", responseStatus);
                modelAndView.addObject("errorCode", errorCode);
                modelAndView.addObject("errorMessage", errorMessage);
            }
            return modelAndView;
        }
    }

    protected Object createJsonResult(HttpServletRequest request, HttpServletResponse response, Throwable throwable) {
        Result<Object> result = new Result<Object>();
        result.setSuccess(false);
        String description = null;
        String code = null;
        if (throwable instanceof BusinessException) {
            BusinessException businessEx = (BusinessException) throwable;
            description = businessEx.getDescription();
            code = businessEx.getCode();
        }
        if (StringUtils.isBlank(description)) {
            description = defaultErrorMessage;
        }
        if (StringUtils.isNotBlank(code)) {
            result.setCode(code);
        }
        if (throwable != null) {
            result.setMessage(description);
        }
        else {
            int respStatus = response.getStatus();
            result.setMessage("An error has occurred. (Response Status: " + respStatus + ") ");
        }
        return result;
    }

    @Override
    public Object handle(HttpServletRequest request, HttpServletResponse response, Throwable throwable) {
        String accept = request.getHeader("Accept");
        accept = StringUtils.isNotBlank(accept) ? accept.toLowerCase() : null;
        if (accept != null && accept.contains(TEXT_HTML)) {
            return this.createPageResult(request, response, throwable);
        }
        else {
            return this.createJsonResult(request, response, throwable);
        }
    }

}

package artoria.exception;

import artoria.common.ErrorCode;
import artoria.common.Result;
import artoria.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import static artoria.common.Constants.*;
import static artoria.common.DefaultErrorCode.INTERNAL_SERVER_ERROR;

/**
 * Default error processor.
 * @author Kahle
 */
public class DefaultErrorProcessor implements ErrorProcessor {
    private static Logger log = LoggerFactory.getLogger(DefaultErrorProcessor.class);
    private static final String ERROR_ALERT = "An error has occurred. ";
    private static final String TEXT_HTML = "text/html";
    private static final String RETRACT = "    ";
    private ExceptionProperties exceptionProperties;

    public DefaultErrorProcessor(ExceptionProperties exceptionProperties) {

        this.exceptionProperties = exceptionProperties;
    }

    protected Object createPageResult(HttpServletRequest request, HttpServletResponse response, Throwable throwable) {
        Boolean showErrorMessage = exceptionProperties.getShowErrorMessage();
        BusinessException businessEx = null; ErrorCode errorCode = null;
        String errorMessage = null;
        if (throwable instanceof BusinessException) {
            businessEx = (BusinessException) throwable;
            errorCode = businessEx.getErrorCode();
        }
        if (businessEx != null && errorCode != null) {
            String code = errorCode.getCode();
            String msg = ErrorCodeMapper.getMessage(code);
            if (msg == null) {
                msg = errorCode.getDescription();
            }
            errorMessage = msg + BLANK_SPACE + LEFT_PARENTHESIS + code + RIGHT_PARENTHESIS;
        }
        else if (throwable != null && showErrorMessage) {
            errorMessage = throwable.getMessage();
        }
        if (exceptionProperties.getInternalErrorPage()) {
            StringBuilder builder = new StringBuilder();
            builder.append("<!DOCTYPE HTML>").append(NEWLINE);
            builder.append("<html>").append(NEWLINE);
            builder.append("<head>").append(NEWLINE);
            builder.append(RETRACT).append("<title>")
                    .append(ERROR_ALERT)
                    .append("</title>")
                    .append(NEWLINE);
            builder.append("</head>").append(NEWLINE);
            builder.append("<body>").append(NEWLINE);
            builder.append(RETRACT).append("<h3>").append(NEWLINE);
            builder.append(RETRACT).append(RETRACT)
                    .append(ERROR_ALERT)
                    .append(NEWLINE);
            builder.append(RETRACT).append("</h3>").append(NEWLINE);
            builder.append(RETRACT).append("Response Status: ")
                    .append(response.getStatus())
                    .append("<br />").append(NEWLINE);
            if (StringUtils.isNotBlank(errorMessage)) {
                builder.append(RETRACT).append("Error Message: ")
                        .append(errorMessage)
                        .append("<br />").append(NEWLINE);
            }
            builder.append(RETRACT)
                    .append("Please check the log for details if necessary. ")
                    .append("<br />").append(NEWLINE);
            builder.append(RETRACT).append("Powered by Artoria. ")
                    .append("<br />").append(NEWLINE);
            builder.append("</body>").append(NEWLINE);
            builder.append("</html>").append(NEWLINE);
            try {
                response.setContentType(TEXT_HTML + ";charset=" + DEFAULT_ENCODING_NAME);
                PrintWriter writer = response.getWriter();
                writer.write(builder.toString());
            }
            catch (IOException e) {
                throw ExceptionUtils.wrap(e);
            }
            return null;
        }
        else {
            int respStatus = response.getStatus();
            String path = exceptionProperties.getBaseTemplatePath();
            path += SLASH + respStatus;
            ModelAndView modelAndView = new ModelAndView(path);
            if (StringUtils.isNotBlank(errorMessage)) {
                modelAndView.addObject("errorMessage", errorMessage);
            }
            return modelAndView;
        }
    }

    protected Object createJsonResult(HttpServletRequest request, HttpServletResponse response, Throwable throwable) {
        BusinessException businessEx = null; ErrorCode errorCode = null;
        Result<Object> result = new Result<Object>();
        result.setSuccess(false);
        if (throwable instanceof BusinessException) {
            businessEx = (BusinessException) throwable;
            errorCode = businessEx.getErrorCode();
        }
        if (businessEx != null && errorCode != null) {
            result.setCode(errorCode.getCode());
            String code = result.getCode();
            String msg = ErrorCodeMapper.getMessage(code);
            if (msg == null) {
                msg = errorCode.getDescription();
            }
            result.setMessage(msg);
        }
        else if (throwable != null) {
            result.setCode(INTERNAL_SERVER_ERROR.getCode());
            result.setMessage(INTERNAL_SERVER_ERROR.getDescription());
            if (exceptionProperties.getShowErrorMessage()) {
                String msg = throwable.getMessage();
                msg = msg == null ? EMPTY_STRING : msg;
                msg = result.getMessage() + " (Error Message: " + msg + ")";
                result.setMessage(msg);
            }
        }
        else {
            int respStatus = response.getStatus();
            result.setMessage(ERROR_ALERT + "(Response Status: " + respStatus + ") ");
        }
        return result;
    }

    @Override
    public Object process(HttpServletRequest request, HttpServletResponse response, Throwable throwable) {
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

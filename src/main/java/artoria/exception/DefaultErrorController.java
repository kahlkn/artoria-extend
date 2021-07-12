package artoria.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Simple error controller.
 * @author Kahle
 */
@Controller
public class DefaultErrorController implements ErrorController {
    private static Logger log = LoggerFactory.getLogger(DefaultErrorController.class);
    private static final String ERROR_PATH = "/error";
    private ErrorAttributes errorAttributes;
    private ErrorHandler errorHandler;

    @Autowired
    public DefaultErrorController(ErrorAttributes errorAttributes, ErrorHandler errorHandler) {
        this.errorAttributes = errorAttributes;
        this.errorHandler = errorHandler;
        log.info("The default error controller was initialized success. ");
    }

    @Override
    public String getErrorPath() {

        return ERROR_PATH;
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = ERROR_PATH, produces = {"text/html"})
    public Object returnPageResult(HttpServletRequest request, HttpServletResponse response) {
        WebRequest webRequest = new ServletWebRequest(request);
        Throwable throwable = errorAttributes.getError(webRequest);
        return errorHandler.handle(request, response, throwable);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = ERROR_PATH)
    public Object returnJsonResult(HttpServletRequest request, HttpServletResponse response) {
        WebRequest webRequest = new ServletWebRequest(request);
        Throwable throwable = errorAttributes.getError(webRequest);
        return errorHandler.handle(request, response, throwable);
    }

}

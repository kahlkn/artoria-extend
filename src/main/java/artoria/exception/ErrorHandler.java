package artoria.exception;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Error handler.
 * @author Kahle
 */
public interface ErrorHandler {

    /**
     * Handle unhandled exceptions, and return the corresponding result.
     * @param request Http request
     * @param response Http response
     * @param throwable Caught exception
     * @return The result of exception handling
     */
    Object handle(HttpServletRequest request, HttpServletResponse response, Throwable throwable);

}

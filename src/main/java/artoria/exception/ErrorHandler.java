package artoria.exception;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * The error handler.
 * @author Kahle
 */
public interface ErrorHandler {

    /**
     * Handle unhandled exceptions, and return the corresponding result.
     * @param request The http request
     * @param response The http response
     * @param throwable The caught exception
     * @return The result of exception handling
     */
    Object handle(HttpServletRequest request, HttpServletResponse response, Throwable throwable);

}

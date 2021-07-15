package artoria.logging;

import artoria.engine.template.PlainTemplateEngine;
import org.apache.log4j.Logger;

import static artoria.common.Constants.TWO;

/**
 * Log4j logger.
 * @author Kahle
 */
public class Log4jLogger implements artoria.logging.Logger {
    private final PlainTemplateEngine templateEngine;
    private final Logger logger;

    public Log4jLogger(Logger logger, PlainTemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
        this.logger = logger;
    }

    private void log(org.apache.log4j.Level level, String format, Object[] arguments, Throwable throwable) {
        if (!logger.isEnabledFor(level)) { return; }
        StackTraceElement element = new Throwable().getStackTrace()[TWO];
        String clazzName = element.getClassName();
        /*StringBuilderWriter writer = new StringBuilderWriter();
        templateEngine.render(arguments, writer, null, format);
        String message = writer.toString();*/
        String message = templateEngine.render(format, arguments);
        logger.log(clazzName, level, message, throwable);
    }

    @Override
    public void trace(String format, Object... arguments) {

        log(org.apache.log4j.Level.TRACE, format, arguments, null);
    }

    @Override
    public void trace(String message, Throwable throwable) {

        log(org.apache.log4j.Level.TRACE, message, null, throwable);
    }

    @Override
    public void debug(String format, Object... arguments) {

        log(org.apache.log4j.Level.DEBUG, format, arguments, null);
    }

    @Override
    public void debug(String message, Throwable throwable) {

        log(org.apache.log4j.Level.DEBUG, message, null, throwable);
    }

    @Override
    public void info(String format, Object... arguments) {

        log(org.apache.log4j.Level.INFO, format, arguments, null);
    }

    @Override
    public void info(String message, Throwable throwable) {

        log(org.apache.log4j.Level.INFO, message, null, throwable);
    }

    @Override
    public void warn(String format, Object... arguments) {

        log(org.apache.log4j.Level.WARN, format, arguments, null);
    }

    @Override
    public void warn(String message, Throwable throwable) {

        log(org.apache.log4j.Level.WARN, message, null, throwable);
    }

    @Override
    public void error(String format, Object... arguments) {

        log(org.apache.log4j.Level.ERROR, format, arguments, null);
    }

    @Override
    public void error(String message, Throwable throwable) {

        log(org.apache.log4j.Level.ERROR, message, null, throwable);
    }

    @Override
    public boolean isTraceEnabled() {

        return logger.isEnabledFor(org.apache.log4j.Level.TRACE);
    }

    @Override
    public boolean isDebugEnabled() {

        return logger.isEnabledFor(org.apache.log4j.Level.DEBUG);
    }

    @Override
    public boolean isInfoEnabled() {

        return logger.isEnabledFor(org.apache.log4j.Level.INFO);
    }

    @Override
    public boolean isWarnEnabled() {

        return logger.isEnabledFor(org.apache.log4j.Level.WARN);
    }

    @Override
    public boolean isErrorEnabled() {

        return logger.isEnabledFor(org.apache.log4j.Level.ERROR);
    }

}

package artoria.logging;

import artoria.util.ClassLoaderUtils;
import artoria.util.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;

/**
 * Logger auto configuration.
 * @author Kahle
 */
@Configuration
public class LoggingAutoConfiguration implements InitializingBean, DisposableBean {
    private static final String SLF4J_BRIDGE_HANDLER_CLASS = "org.slf4j.bridge.SLF4JBridgeHandler";
    private static Logger log = LoggerFactory.getLogger(LoggingAutoConfiguration.class);

    private void initJulToSlf4j() {
        ClassLoader classLoader = ClassLoaderUtils.getDefaultClassLoader();
        if (!ClassUtils.isPresent(SLF4J_BRIDGE_HANDLER_CLASS, classLoader)) {
            return;
        }
        if (SLF4JBridgeHandler.isInstalled()) {
            log.info("The jul to slf4j already initialized. ");
            return;
        }
        // Optionally remove existing handlers attached to j.u.l root logger
        // (since SLF4J 1.6.5)
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        // add SLF4JBridgeHandler to j.u.l's root logger, should be done once during
        // the initialization phase of your application
        SLF4JBridgeHandler.install();
        log.info("The jul to slf4j was initialized success. ");
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        initJulToSlf4j();
    }

    @Override
    public void destroy() throws Exception {
    }

}

package artoria.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;

import static artoria.common.Constants.DEFAULT_CHARSET_NAME;
import static artoria.common.Constants.SIXTEEN;
import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

/**
 * Spring auto configuration.
 * @author Kahle
 */
@Configuration
@AutoConfigureOrder(HIGHEST_PRECEDENCE + SIXTEEN)
public class SpringAutoConfiguration implements ApplicationContextAware, InitializingBean, DisposableBean {
    private static Logger log = LoggerFactory.getLogger(SpringAutoConfiguration.class);

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        ApplicationContextUtils.setContext(applicationContext);
        log.info("The application context tools was initialized success. ");
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        log.info("The default charset for the current run environment is {}. ", DEFAULT_CHARSET_NAME);
    }

    @Override
    public void destroy() throws Exception {
    }

}

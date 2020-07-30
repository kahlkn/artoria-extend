package artoria.exchange;

import artoria.util.ClassLoaderUtils;
import artoria.util.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;

/**
 * Xml auto configuration.
 * @author Kahle
 */
@Configuration
public class XmlAutoConfiguration implements InitializingBean, DisposableBean {
    private static final String X_STREAM_CLASS = "com.thoughtworks.xstream.XStream";
    private static Logger log = LoggerFactory.getLogger(XmlAutoConfiguration.class);

    @Override
    public void afterPropertiesSet() throws Exception {
        ClassLoader classLoader = ClassLoaderUtils.getDefaultClassLoader();
        if (ClassUtils.isPresent(X_STREAM_CLASS, classLoader)) {
            XmlUtils.setXmlProvider(new XStreamXmlProvider());
        }
//        else if (ClassUtils.isPresent("", classLoader)) {
//            XmlUtils.setXmlProvider();
//        }
        else {
            log.warn("Can not found \"xstream\", will keep default. ");
        }
    }

    @Override
    public void destroy() throws Exception {
    }

}

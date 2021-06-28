package artoria.codec;

import artoria.util.ClassLoaderUtils;
import artoria.util.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;

import static artoria.common.Constants.MINUS_ONE;
import static java.lang.Boolean.TRUE;

/**
 * Base64 auto configuration.
 * @see org.apache.commons.codec.binary.Base64
 * @author Kahle
 */
@Configuration
public class Base64AutoConfiguration implements InitializingBean, DisposableBean {
    private static final String APACHE_BASE64 = "org.apache.commons.codec.binary.Base64";
    private static final String JAVA_BASE64 = "java.util.Base64";
    private static Logger log = LoggerFactory.getLogger(Base64AutoConfiguration.class);

    @Override
    public void afterPropertiesSet() throws Exception {
        ClassLoader classLoader = ClassLoaderUtils.getDefaultClassLoader();
        if (ClassUtils.isPresent(APACHE_BASE64, classLoader)) {
            // If have Apache Commons Codec, to use it.
            Base64Utils.setBase64(new ApacheBase64());
            Base64Utils.setUrlBase64(new ApacheBase64(TRUE));
            Base64Utils.setMimeBase64(new ApacheBase64(TRUE, MINUS_ONE, null));
        }
        else if (ClassUtils.isPresent(JAVA_BASE64, classLoader)) {
            // If have "java.util.Base64", to use it.
            Base64Utils.setBase64(new Java8Base64());
            Base64Utils.setUrlBase64(new Java8Base64(TRUE));
            Base64Utils.setMimeBase64(new Java8Base64(TRUE, MINUS_ONE, null));
        }
        log.info("The base64 tools was initialized success. ");
    }

    @Override
    public void destroy() throws Exception {
    }

}

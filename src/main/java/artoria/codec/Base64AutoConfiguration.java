package artoria.codec;

import artoria.util.ClassLoaderUtils;
import artoria.util.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Base64 auto configuration.
 * @see org.apache.commons.codec.binary.Base64
 * @author Kahle
 */
@Configuration
public class Base64AutoConfiguration implements InitializingBean, DisposableBean {
    private static final String APACHE_BASE64 = "org.apache.commons.codec.binary.Base64";
    private static Logger log = LoggerFactory.getLogger(Base64AutoConfiguration.class);
    private Base64 urlSafeBase64;
    private Base64 base64;

    @Override
    public void afterPropertiesSet() throws Exception {
        ClassLoader classLoader = ClassLoaderUtils.getDefaultClassLoader();
        // If have Apache Commons Codec, to use it.
        boolean haveApache = ClassUtils.isPresent(APACHE_BASE64, classLoader);
        base64 = haveApache ? new ApacheBase64() : new Base64();
        urlSafeBase64 = haveApache ? new ApacheBase64() : new Base64();
        urlSafeBase64.setUrlSafe(true);
        Base64Utils.setBase64(base64);
        log.info("The base64 tools was initialized success. ");
    }

    @Override
    public void destroy() throws Exception {
    }

    @Bean
    public Base64 base64() {

        return base64;
    }

    @Bean
    public Base64 urlSafeBase64() {

        return urlSafeBase64;
    }

    private static class ApacheBase64 extends Base64 {
        private org.apache.commons.codec.binary.Base64 apacheBase64;

        private org.apache.commons.codec.binary.Base64 getBase64() {
            if (apacheBase64 != null) {
                return apacheBase64;
            }
            synchronized (this) {
                if (apacheBase64 != null) {
                    return apacheBase64;
                }
                int lineLength = this.getLineLength();
                lineLength = lineLength > 0 ? lineLength : DEFAULT_LINE_LENGTH;
                byte[] lineSeparator = this.getLineSeparator();
                boolean urlSafe = this.isUrlSafe();
                apacheBase64 = new org.apache.commons.codec.binary.Base64(
                        lineLength, lineSeparator, urlSafe
                );
                return apacheBase64;
            }
        }

        @Override
        public byte[] encode(byte[] source) {

            return this.getBase64().encode(source);
        }

        @Override
        public byte[] decode(byte[] source) {

            return this.getBase64().decode(source);
        }

    }

}

package artoria.codec;

import artoria.util.ClassLoaderUtils;
import artoria.util.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;

import static artoria.common.Constants.MINUS_ONE;
import static artoria.common.Constants.ZERO;

/**
 * Base64 auto configuration.
 * @see org.apache.commons.codec.binary.Base64
 * @author Kahle
 */
@Configuration
public class Base64AutoConfiguration implements InitializingBean, DisposableBean {
    private static final String APACHE_BASE64 = "org.apache.commons.codec.binary.Base64";
    private static Logger log = LoggerFactory.getLogger(Base64AutoConfiguration.class);

    @Override
    public void afterPropertiesSet() throws Exception {
        ClassLoader classLoader = ClassLoaderUtils.getDefaultClassLoader();
        // If have Apache Commons Codec, to use it.
        boolean haveApache = ClassUtils.isPresent(APACHE_BASE64, classLoader);
        if (haveApache) {
            Base64Utils.setFactory(new Base64Utils.Base64Factory() {
                @Override
                public Base64 getInstance() {
                    return new ApacheBase64();
                }
                @Override
                public Base64 getInstance(boolean urlSafe) {
                    return new ApacheBase64(urlSafe);
                }
                @Override
                public Base64 getInstance(boolean mime, int lineLength, byte[] lineSeparator) {
                    return new ApacheBase64(mime, lineLength, lineSeparator);
                }
            });
            log.info("The base64 tools was initialized success. ");
        }
    }

    @Override
    public void destroy() throws Exception {
    }

    private static class ApacheBase64 extends Base64 {
        private org.apache.commons.codec.binary.Base64 apacheBase64;

        public ApacheBase64() {

            this(false, false, MINUS_ONE, null);
        }

        public ApacheBase64(boolean urlSafe) {

            this(urlSafe, false, MINUS_ONE, null);
        }

        public ApacheBase64(boolean mime, int lineLength, byte[] lineSeparator) {

            this(false, mime, lineLength, lineSeparator);
        }

        public ApacheBase64(boolean urlSafe, boolean mime, int lineLength, byte[] lineSeparator) {
            super(urlSafe, mime, lineLength, lineSeparator);
            if (mime) {
                lineLength = lineLength > ZERO ? lineLength : MIME_LINE_LENGTH;
            }
            else { lineLength = MINUS_ONE; }
            apacheBase64 = new org.apache.commons.codec.binary.Base64(
                    lineLength, lineSeparator, urlSafe
            );
        }

        @Override
        public byte[] encode(byte[] source) {

            return apacheBase64.encode(source);
        }

        @Override
        public byte[] decode(byte[] source) {

            return apacheBase64.decode(source);
        }

    }

}

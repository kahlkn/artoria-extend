package artoria.crypto;

import artoria.reflect.ReflectUtils;
import artoria.util.ClassLoaderUtils;
import artoria.util.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.Provider;
import java.security.Security;

import static artoria.common.Constants.*;

/**
 * Crypto auto configuration.
 * @author Kahle
 */
@Configuration
public class CryptoAutoConfiguration implements InitializingBean, DisposableBean {
    private static final String BOUNCY_CASTLE_CLASS = "org.bouncycastle.jce.provider.BouncyCastleProvider";
    private static Logger log = LoggerFactory.getLogger(CryptoAutoConfiguration.class);

    private void loadBouncyCastle() {
        ClassLoader loader = ClassLoaderUtils.getDefaultClassLoader();
        if (!ClassUtils.isPresent(BOUNCY_CASTLE_CLASS, loader)) {
            return;
        }
        try {
            Class<?> loadClass =
                    ClassLoaderUtils.loadClass(BOUNCY_CASTLE_CLASS, this.getClass());
            Object o = ReflectUtils.newInstance(loadClass);
            Provider provider = (Provider) o;
            Security.addProvider(provider);
            String name = provider.getClass().getName();
            double version = provider.getVersion();
            log.info("The bouncy castle provider was initialized success. ", name, version);
        }
        catch (Exception e) {
            log.warn("The bouncy castle provider was initialized error. ", e);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        this.loadBouncyCastle();
    }

    @Override
    public void destroy() throws Exception {
    }

    @Bean
    public Hash md2() {

        return new Hash(MD2);
    }

    @Bean
    public Hash md5() {

        return new Hash(MD5);
    }

    @Bean
    public Hash sha1() {

        return new Hash(SHA1);
    }

    @Bean
    public Hash sha256() {

        return new Hash(SHA256);
    }

    @Bean
    public Hash sha384() {

        return new Hash(SHA384);
    }

    @Bean
    public Hash sha512() {

        return new Hash(SHA512);
    }

}

package artoria.beans;

import artoria.util.ClassLoaderUtils;
import artoria.util.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;

/**
 * Bean tools auto configuration.
 * @author Kahle
 */
@Configuration
public class BeanAutoConfiguration implements InitializingBean, DisposableBean {
    private static final String APACHE_BEAN_TOOLS_CLASS = "org.apache.commons.beanutils.BeanUtils";
    private static final String SPRING_BEAN_TOOLS_CLASS = "org.springframework.beans.BeanUtils";
    private static final String SPRING_CGLIB_CLASS = "org.springframework.cglib.proxy.MethodInterceptor";
    private static final String CGLIB_CLASS = "net.sf.cglib.proxy.MethodInterceptor";
    private static Logger log = LoggerFactory.getLogger(BeanAutoConfiguration.class);

    @Override
    public void afterPropertiesSet() throws Exception {
        ClassLoader classLoader = ClassLoaderUtils.getDefaultClassLoader();
        Class<? extends BeanMap> mapType = null;
        BeanCopier beanCopier = null;
        if (ClassUtils.isPresent(SPRING_CGLIB_CLASS, classLoader)) {
            beanCopier = new SpringCglibBeanCopier();
            mapType = SpringCglibBeanMap.class;
            log.info("The spring cglib bean tools was initialized success. ");
        }
        else if (ClassUtils.isPresent(CGLIB_CLASS, classLoader)) {
            beanCopier = new CglibBeanCopier();
            mapType = CglibBeanMap.class;
            log.info("The cglib bean tools was initialized success. ");
        }
        else if (ClassUtils.isPresent(SPRING_BEAN_TOOLS_CLASS, classLoader)) {
            beanCopier = new SpringBeanCopier();
            mapType = SimpleBeanMap.class;
            log.info("The spring bean tools was initialized success. ");
        }
        else if (ClassUtils.isPresent(APACHE_BEAN_TOOLS_CLASS, classLoader)) {
            beanCopier = new ApacheBeanCopier();
            mapType = SimpleBeanMap.class;
            log.info("The apache bean tools was initialized success. ");
        }
        else {
            // Do nothing.
        }
        if (mapType != null) { BeanUtils.setMapType(mapType); }
        if (beanCopier != null) { BeanUtils.setBeanCopier(beanCopier); }
    }

    @Override
    public void destroy() throws Exception {
    }

}

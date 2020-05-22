package artoria.spring;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Spring project "ApplicationContext" tools.
 * @author Kahle
 */
public class ApplicationContextUtils {
    private static ApplicationContext context = null;

    public static ApplicationContext getContext() {

        return context;
    }

    public static void setContext(ApplicationContext context) {

        ApplicationContextUtils.context = context;
    }

    public static Object getBean(String name) {

        return getContext().getBean(name);
    }

    public static Object getBean(String name, Object... args) {

        return getContext().getBean(name, args);
    }

    public static <T> T getBean(String name, Class<T> requiredType) {

        return getContext().getBean(name, requiredType);
    }

    public static <T> T getBean(Class<T> requiredType) {

        return getContext().getBean(requiredType);
    }

    public static <T> T getBean(Class<T> requiredType, Object... args) {

        return getContext().getBean(requiredType, args);
    }

    public static void registerBean(String name, BeanDefinitionBuilder beanDefinitionBuilder) {
        // Beans registered by this method can only be obtained by "ApplicationContext.getBean"
        //      and cannot be obtained by "Autowired".
        // "BeanDefinitionBuilder.rootBeanDefinition(clazz);"      parent   bean
        // "BeanDefinitionBuilder.childBeanDefinition(clazz);"     child    bean
        // "BeanDefinitionBuilder.genericBeanDefinition(clazz);"   generic  bean
        BeanDefinition beanDefinition = beanDefinitionBuilder.getBeanDefinition();
        ConfigurableApplicationContext applicationContext = (ConfigurableApplicationContext) getContext();
        BeanDefinitionRegistry beanDefinitionRegistry = (BeanDefinitionRegistry) applicationContext.getBeanFactory();
        beanDefinitionRegistry.registerBeanDefinition(name, beanDefinition);
    }

}

package artoria.exchange;

import artoria.util.ClassLoaderUtils;
import artoria.util.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;

/**
 * Json auto configuration.
 * @author Kahle
 */
@Configuration
public class JsonAutoConfiguration implements InitializingBean, DisposableBean {
    private static final String FASTJSON_CLASS = "com.alibaba.fastjson.JSON";
    private static final String GSON_CLASS = "com.google.gson.Gson";
    private static Logger log = LoggerFactory.getLogger(JsonAutoConfiguration.class);

    @Override
    public void afterPropertiesSet() throws Exception {
        ClassLoader classLoader = ClassLoaderUtils.getDefaultClassLoader();
        if (ClassUtils.isPresent(FASTJSON_CLASS, classLoader)) {
            JsonUtils.setJsonProvider(new FastJsonProvider());
        }
        else if (ClassUtils.isPresent(GSON_CLASS, classLoader)) {
            JsonUtils.setJsonProvider(new GsonProvider());
        }
        else {
            log.warn("Can not found \"fastjson\" or \"gson\", will keep default. ");
        }
    }

    @Override
    public void destroy() throws Exception {
    }

}

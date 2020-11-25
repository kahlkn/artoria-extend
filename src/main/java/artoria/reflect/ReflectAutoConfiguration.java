package artoria.reflect;

import artoria.aop.Enhancer;
import artoria.aop.Interceptor;
import artoria.beans.BeanAutoConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ConcurrentReferenceHashMap;

import java.lang.reflect.Method;
import java.util.*;

import static org.springframework.util.ConcurrentReferenceHashMap.ReferenceType.WEAK;

/**
 * Reflect auto configuration.
 * @author Kahle
 */
@Configuration
@AutoConfigureAfter(BeanAutoConfiguration.class)
public class ReflectAutoConfiguration implements InitializingBean, DisposableBean {
    private static Logger log = LoggerFactory.getLogger(ReflectAutoConfiguration.class);
    private static final List<String> METHOD_NAMES;

    static {
        // TODO: Do cache optimize by cacheManager
        List<String> list = new ArrayList<String>();
        Collections.addAll(list, "findConstructors"
                , "findConstructor"
                , "findFields", "findDeclaredFields"
                , "findAccessFields", "findField"
                , "findMethods", "findDeclaredMethods"
                , "findAccessMethods", "findReadMethods"
                , "findWriteMethods", "findMethod"
                , "findSimilarMethod");
        METHOD_NAMES = Collections.unmodifiableList(list);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        ReflectProvider reflectProvider = ReflectUtils.getReflectProvider();
        if (reflectProvider != null) {
            ReflectProviderInterceptor intr = new ReflectProviderInterceptor(reflectProvider);
            ReflectProvider instance = (ReflectProvider) Enhancer.enhance(reflectProvider, intr);
            ReflectUtils.setReflectProvider(instance);
            log.info("Add cache to reflect provider success. ");
        }
    }

    @Override
    public void destroy() throws Exception {
    }

    private static class ReflectProviderInterceptor implements Interceptor {
        private Map<String, Object> cache;
        private ReflectProvider original;

        ReflectProviderInterceptor(ReflectProvider original) {
            this.cache = new ConcurrentReferenceHashMap<String, Object>(64, WEAK);
            this.original = original;
        }

        @Override
        public Object intercept(Object proxyObject, Method method, Object[] args) throws Throwable {
            if (METHOD_NAMES.contains(method.getName())) {
                String key = method.getName() + Arrays.toString(args);
                Object val = this.cache.get(key);
                if (val == null) {
                    val = method.invoke(this.original, args);
                    this.cache.put(key, val);
                }
                return val;
            }
            else {
                return method.invoke(this.original, args);
            }
        }

    }

}

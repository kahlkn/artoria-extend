package artoria.aop;

import artoria.util.Assert;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * Proxy factory implements by cglib.
 * @author Kahle
 */
public class CglibProxyFactory implements ProxyFactory {

    private static class MethodInterceptorAdapter implements MethodInterceptor {
        private final Interceptor interceptor;

        public MethodInterceptorAdapter(Interceptor interceptor) {
            Assert.notNull(interceptor, "Parameter \"interceptor\" must not null. ");
            this.interceptor = interceptor;
        }

        @Override
        public Object intercept(Object proxyObject, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {

            return interceptor.intercept(proxyObject, method, args);
        }

    }

    @Override
    public Object getInstance(Class<?> originalClass, Interceptor interceptor) {
        Assert.notNull(originalClass, "Parameter \"targetClass\" must not null. ");
        Assert.notNull(interceptor, "Parameter \"interceptor\" must not null. ");
        MethodInterceptor methodInterceptor = new MethodInterceptorAdapter(interceptor);
        return Enhancer.create(originalClass, methodInterceptor);
    }

}

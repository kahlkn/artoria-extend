package artoria.spring;

import com.netflix.hystrix.HystrixThreadPoolKey;
import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategy;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestVariable;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestVariableLifecycle;
import com.netflix.hystrix.strategy.properties.HystrixProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Spring request context holder hystrix concurrency strategy.
 * @author Kahle
 */
public class RequestContextHystrixConcurrencyStrategy extends HystrixConcurrencyStrategy {
    private static final Logger log = LoggerFactory.getLogger(RequestContextHystrixConcurrencyStrategy.class);
    private HystrixConcurrencyStrategy existingConcurrencyStrategy;

    public RequestContextHystrixConcurrencyStrategy(HystrixConcurrencyStrategy existingConcurrencyStrategy) {

        this.existingConcurrencyStrategy = existingConcurrencyStrategy;
    }

    @Override
    public ThreadPoolExecutor getThreadPool(HystrixThreadPoolKey threadPoolKey,
                                            HystrixProperty<Integer> corePoolSize,
                                            HystrixProperty<Integer> maximumPoolSize,
                                            HystrixProperty<Integer> keepAliveTime,
                                            TimeUnit unit,
                                            BlockingQueue<Runnable> workQueue) {
        return existingConcurrencyStrategy == null
                ? super.getThreadPool(threadPoolKey, corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue)
                : existingConcurrencyStrategy.getThreadPool(threadPoolKey, corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    @Override
    public BlockingQueue<Runnable> getBlockingQueue(int maxQueueSize) {
        return existingConcurrencyStrategy == null
                ? super.getBlockingQueue(maxQueueSize)
                : existingConcurrencyStrategy.getBlockingQueue(maxQueueSize);
    }

    @Override
    public <T> Callable<T> wrapCallable(Callable<T> callable) {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        Callable<T> wrappedCallable = new WrappedCallable<T>(callable, requestAttributes);
        return existingConcurrencyStrategy == null
                ? super.wrapCallable(wrappedCallable)
                : existingConcurrencyStrategy.wrapCallable(wrappedCallable);
    }

    @Override
    public <T> HystrixRequestVariable<T> getRequestVariable(HystrixRequestVariableLifecycle<T> rv) {
        return existingConcurrencyStrategy == null
                ? super.getRequestVariable(rv)
                : existingConcurrencyStrategy.getRequestVariable(rv);
    }

    private static class WrappedCallable<T> implements Callable<T> {
        private final RequestAttributes requestAttributes;
        private final Callable<T> delegate;

        WrappedCallable(Callable<T> delegate, RequestAttributes requestAttributes) {
            this.requestAttributes = requestAttributes;
            this.delegate = delegate;
        }

        @Override
        public T call() throws Exception {
            try {
                RequestContextHolder.setRequestAttributes(requestAttributes);
                return delegate.call();
            }
            finally {
                RequestContextHolder.resetRequestAttributes();
            }
        }

    }

}

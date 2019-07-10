package artoria.spring;

import com.netflix.hystrix.Hystrix;
import com.netflix.hystrix.strategy.HystrixPlugins;
import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategy;
import com.netflix.hystrix.strategy.eventnotifier.HystrixEventNotifier;
import com.netflix.hystrix.strategy.executionhook.HystrixCommandExecutionHook;
import com.netflix.hystrix.strategy.metrics.HystrixMetricsPublisher;
import com.netflix.hystrix.strategy.properties.HystrixPropertiesStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;

/**
 * Request context holder hystrix concurrency strategy auto configuration.
 * @author Kahle
 */
@Configuration
@ConditionalOnClass({Hystrix.class})
public class RequestContextHystrixAutoConfiguration implements InitializingBean, DisposableBean {
    private static final Logger log = LoggerFactory.getLogger(RequestContextHystrixConcurrencyStrategy.class);

    @Override
    public void afterPropertiesSet() throws Exception {
        HystrixPlugins hystrixPlugins = HystrixPlugins.getInstance();
        // Keeps references of existing Hystrix plugins.
        HystrixConcurrencyStrategy existingConcurrencyStrategy = hystrixPlugins.getConcurrencyStrategy();
        HystrixEventNotifier eventNotifier = hystrixPlugins.getEventNotifier();
        HystrixMetricsPublisher metricsPublisher = hystrixPlugins.getMetricsPublisher();
        HystrixPropertiesStrategy propertiesStrategy = hystrixPlugins.getPropertiesStrategy();
        HystrixCommandExecutionHook commandExecutionHook = hystrixPlugins.getCommandExecutionHook();
        // Reset.
        HystrixPlugins.reset();
        // Registers existing plugins excepts the Concurrent Strategy plugin.
        HystrixConcurrencyStrategy concurrencyStrategyProxy =
                new RequestContextHystrixConcurrencyStrategy(existingConcurrencyStrategy);
        hystrixPlugins.registerConcurrencyStrategy(concurrencyStrategyProxy);
        hystrixPlugins.registerEventNotifier(eventNotifier);
        hystrixPlugins.registerMetricsPublisher(metricsPublisher);
        hystrixPlugins.registerPropertiesStrategy(propertiesStrategy);
        hystrixPlugins.registerCommandExecutionHook(commandExecutionHook);
        log.info("The request context holder hystrix concurrency strategy was initialized success. ");
    }

    @Override
    public void destroy() throws Exception {
    }

}

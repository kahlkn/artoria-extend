package artoria.message;

import artoria.lifecycle.LifecycleUtils;
import artoria.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Collection;
import java.util.Map;

@Configuration
@AutoConfigureAfter({RedisAutoConfiguration.class})
@AutoConfigureBefore({SimpleMessageAutoConfiguration.class})
@ConditionalOnClass({RedisOperations.class})
@ConditionalOnMissingBean(name = {"messageProvider", "messageProducer", "messageConsumer"})
public class RedisMessageAutoConfiguration implements DisposableBean {
    private static Logger log = LoggerFactory.getLogger(RedisMessageAutoConfiguration.class);
    private final MessageProvider messageProvider;

    @Autowired
    public RedisMessageAutoConfiguration(StringRedisTemplate stringRedisTemplate) {
        // TODO : Need to reconsider
        messageProvider = new RedisMessageProvider(stringRedisTemplate);
        MessageUtils.setMessageProvider(messageProvider);
    }

    @Bean
    @ConditionalOnMissingBean(name = "messageProvider")
    public MessageProvider messageProvider(ApplicationContext applicationContext) {
        Map<String, TargetedMessageListener> map =
                applicationContext.getBeansOfType(TargetedMessageListener.class);
        Collection<TargetedMessageListener> listeners = map.values();
        for (TargetedMessageListener listener : listeners) {
            if (listener == null) { continue; }
            String destination = listener.getDestination();
            Map<String, Object> properties = listener.getParameters();
            if (StringUtils.isBlank(destination)) { continue; }
            messageProvider.listening(destination, properties, listener);
        }
        return messageProvider;
    }

    @Override
    public void destroy() throws Exception {

        LifecycleUtils.destroy(messageProvider);
    }

}

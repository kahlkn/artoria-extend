package artoria.message;

import artoria.lifecycle.LifecycleUtils;
import artoria.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;
import java.util.Map;

@Configuration
@ConditionalOnMissingBean(name = {"messageProvider"})
public class SimpleMessageAutoConfiguration implements DisposableBean {
    private static Logger log = LoggerFactory.getLogger(SimpleMessageAutoConfiguration.class);
    private final MessageProvider messageProvider;

    @Autowired
    public SimpleMessageAutoConfiguration() {
        messageProvider = new SimpleMessageProvider();
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
            Map<String, Object> properties = listener.getProperties();
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

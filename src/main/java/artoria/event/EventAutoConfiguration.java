package artoria.event;

import artoria.util.Assert;
import artoria.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Event auto configuration.
 * @author Kahle
 */
@Configuration
@Import({EventRecordAspect.class})
@ConditionalOnProperty(name = "artoria.event.enabled", havingValue = "true")
@EnableConfigurationProperties({EventProperties.class})
public class EventAutoConfiguration {
    private static Logger log = LoggerFactory.getLogger(EventAutoConfiguration.class);
    @Value("${spring.application.name:unknown}")
    private String serverAppId;

    @Autowired
    public EventAutoConfiguration(EventProperties eventProperties) {
        Assert.notNull(eventProperties, "Parameter \"eventProperties\" must not null. ");
        //this.eventProperties = eventProperties;
    }

    @Bean
    public EventProvider eventProvider(EventProperties eventProperties) {
        Assert.notNull(eventProperties, "Parameter \"eventProperties\" must not null. ");
        String subdivision = eventProperties.getSubdivision();
        String destination = eventProperties.getDestination();
        String anonymousIdName = eventProperties.getAnonymousIdName();
        String tokenIdName = eventProperties.getTokenIdName();
        String clientAppIdName = eventProperties.getClientAppIdName();
        if (StringUtils.isBlank(destination)) {
            destination = "event_record";
        }
        EventProvider eventProvider = new HttpEventProvider(subdivision,
                destination, serverAppId, anonymousIdName, tokenIdName, clientAppIdName);
        EventUtils.setEventProvider(eventProvider);
        log.info("Event tools set destination success. ");
        return eventProvider;
    }

}

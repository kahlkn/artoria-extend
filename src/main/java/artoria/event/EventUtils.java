package artoria.event;

import artoria.util.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class EventUtils {
    private static final EventProvider DEFAULT_EVENT_PROVIDER = new SimpleEventProvider();
    private static Logger log = LoggerFactory.getLogger(EventUtils.class);
    private static EventProvider eventProvider;

    public static EventProvider getEventProvider() {

        return eventProvider != null ? eventProvider : DEFAULT_EVENT_PROVIDER;
    }

    public static void setEventProvider(EventProvider eventProvider) {
        Assert.notNull(eventProvider, "Parameter \"eventProvider\" must not null. ");
        log.info("Set event provider: {}", eventProvider.getClass().getName());
        EventUtils.eventProvider = eventProvider;
    }

    public static void addEvent(String eventName, String eventType, String distinctId) {

        getEventProvider().addEvent(eventName, eventType, distinctId, null, null);
    }

    public static void addEvent(String eventName, String eventType, String distinctId, String anonymousId) {

        getEventProvider().addEvent(eventName, eventType, distinctId, anonymousId, null);
    }

    public static void addEvent(String eventName, String eventType, String distinctId, Map<String, Object> properties) {

        getEventProvider().addEvent(eventName, eventType, distinctId, null, properties);
    }

    public static void addEvent(String eventName, String eventType, String distinctId, String anonymousId, Map<String, Object> properties) {

        getEventProvider().addEvent(eventName, eventType, distinctId, anonymousId, properties);
    }

}

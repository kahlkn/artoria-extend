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

    public static void addEvent(String event, String userId, Map<String, Object> properties) {

        getEventProvider().addEvent(event, null, null, userId, null, properties);
    }

    public static void addEvent(String event, String userId, String anonymousId, Map<String, Object> properties) {

        getEventProvider().addEvent(event, null, null, userId, anonymousId, properties);
    }

    public static void addEvent(String event, String type, String userId, String anonymousId, Map<String, Object> properties) {

        getEventProvider().addEvent(event, type, null, userId, anonymousId, properties);
    }

    public static void addEvent(String event, String type, Long time, String userId, String anonymousId, Map<String, Object> properties) {

        getEventProvider().addEvent(event, type, time, userId, anonymousId, properties);
    }

}

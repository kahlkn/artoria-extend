package artoria.event;

import java.util.Map;

public interface EventProvider {

    /**
     * Who did what and when.
     */
    void addEvent(String eventName, String eventType, String distinctId, String anonymousId, Map<String, Object> properties);

}

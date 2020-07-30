package artoria.event;

import java.util.Map;

public interface EventProvider {

    /**
     * Who did what and when.
     * @param event
     * @param type
     * @param time
     * @param userId
     * @param anonymousId
     * @param properties
     */
    void addEvent(String event, String type, Long time, String userId, String anonymousId, Map<String, Object> properties);

}

package artoria.event;

import java.util.Map;

public interface EventProvider {

    /**
     * Who did what and when.
     */
    void addEvent(String event, String type, Long time, String userId, String anonymousId, Map<String, Object> properties);

}

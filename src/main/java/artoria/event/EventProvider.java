package artoria.event;

import java.util.Map;

public interface EventProvider {

    /**
     * Submit an event (Who did what and when).
     * @param name The event name (event identifier), indicating a certain type of event (such as user login events)
     * @param type The type of the event, such as application, security, system, and so on
     * @param distinctId The distinct id, such as the user's unique id, and so on
     * @param properties The other event properties
     */
    void submit(String name, String type, String distinctId, Map<String, Object> properties);

    /**
     * Submit an event (Who did what and when).
     * @param name The event name (event identifier), indicating a certain type of event (such as user login events)
     * @param type The type of the event, such as application, security, system, and so on
     * @param distinctId The distinct id, such as the user's unique id, and so on
     * @param anonymousId The anonymous id, such as visitor id or token id, etc
     * @param properties The other event properties
     */
    void submit(String name, String type, String distinctId, String anonymousId, Map<String, Object> properties);

    /**
     * Submit an event (Who did what and when).
     * @param name The event name (event identifier), indicating a certain type of event (such as user login events)
     * @param type The type of the event, such as application, security, system, and so on
     * @param time The time at which the event occurred
     * @param distinctId The distinct id, such as the user's unique id, and so on
     * @param anonymousId The anonymous id, such as visitor id or token id, etc
     * @param properties The other event properties
     */
    void submit(String name, String type, Long time, String distinctId, String anonymousId, Map<String, Object> properties);

}

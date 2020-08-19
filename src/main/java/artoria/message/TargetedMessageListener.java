package artoria.message;

import java.util.Map;

/**
 * Targeted message listener.
 * @author Kahle
 */
public interface TargetedMessageListener extends MessageListener {

    /**
     * Get the queue name or get the routing key.
     * @return The queue name or the routing key
     */
    String getDestination();

    /**
     * Get the properties.
     * @return The properties
     */
    Map<String, Object> getProperties();

}

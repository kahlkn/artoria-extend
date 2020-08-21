package artoria.message;

import java.util.Map;

/**
 * Targeted message listener.
 * @author Kahle
 */
public interface TargetedMessageListener extends MessageListener {

    /**
     * Get the destination.
     * @return The destination the message is expected to arrive at
     */
    String getDestination();

    /**
     * Get the properties.
     * @return Supplement any fields that may appear that need to be passed
     */
    Map<String, Object> getProperties();

}

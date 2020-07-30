package artoria.message;

/**
 * Targeted message listener.
 * @author Kahle
 */
public interface TargetedMessageListener extends MessageListener {

    /**
     * Get the subdivision or get the exchange.
     * @return The subdivision or the exchange
     */
    String getSubdivision();

    /**
     * Get the queue name or get the routing key.
     * @return The queue name or the routing key
     */
    String getDestination();

}

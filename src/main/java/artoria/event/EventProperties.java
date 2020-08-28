package artoria.event;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Event properties.
 * @author Kahle
 */
@ConfigurationProperties("artoria.event")
public class EventProperties {
    /**
     * Enabled event tools.
     */
    private Boolean enabled;
    /**
     * The destination where the event is sent.
     */
    private String destination;
    /**
     * Used to distinguish users, equivalent to visitor id.
     */
    private String anonymousIdName = "anonymousId";
    /**
     * Token id (not user id) property name.
     */
    private String tokenIdName = "Authorization";
    /**
     * Client application id property name.
     */
    private String clientAppIdName = "clientAppId";

    public Boolean getEnabled() {

        return enabled;
    }

    public void setEnabled(Boolean enabled) {

        this.enabled = enabled;
    }

    public String getDestination() {

        return destination;
    }

    public void setDestination(String destination) {

        this.destination = destination;
    }

    public String getAnonymousIdName() {

        return anonymousIdName;
    }

    public void setAnonymousIdName(String anonymousIdName) {

        this.anonymousIdName = anonymousIdName;
    }

    public String getTokenIdName() {

        return tokenIdName;
    }

    public void setTokenIdName(String tokenIdName) {

        this.tokenIdName = tokenIdName;
    }

    public String getClientAppIdName() {

        return clientAppIdName;
    }

    public void setClientAppIdName(String clientAppIdName) {

        this.clientAppIdName = clientAppIdName;
    }

}

package artoria.user;

import artoria.data.AbstractExtraData;

import java.io.Serializable;

/**
 * Token object.
 * @author Kahle
 */
public class Token extends AbstractExtraData implements Serializable {
    /**
     * Token id.
     */
    private String id;
    /**
     * User id.
     */
    private String userId;
    /**
     * User agent.
     * @see <a href="https://en.wikipedia.org/wiki/User_agent">User agent</a>
     */
    private String userAgent;
    /**
     * Access time.
     */
    private String accessTime;
    /**
     * Client application id.
     */
    private String clientAppId;
    /**
     * Client network address.
     */
    private String clientNetAddress;

    public String getId() {

        return id;
    }

    public void setId(String id) {

        this.id = id;
    }

    public String getUserId() {

        return userId;
    }

    public void setUserId(String userId) {

        this.userId = userId;
    }

    public String getUserAgent() {

        return userAgent;
    }

    public void setUserAgent(String userAgent) {

        this.userAgent = userAgent;
    }

    public String getAccessTime() {

        return accessTime;
    }

    public void setAccessTime(String accessTime) {

        this.accessTime = accessTime;
    }

    public String getClientAppId() {

        return clientAppId;
    }

    public void setClientAppId(String clientAppId) {

        this.clientAppId = clientAppId;
    }

    public String getClientNetAddress() {

        return clientNetAddress;
    }

    public void setClientNetAddress(String clientNetAddress) {

        this.clientNetAddress = clientNetAddress;
    }

}

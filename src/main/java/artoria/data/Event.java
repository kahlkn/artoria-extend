package artoria.data;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * Event, Who, When, Where, What.
 * @author Kahle
 */
public class Event implements Serializable {
    private Map<String, Object> variables;
    private String id;
    private String tokenId;
    private String userId;
    private Date   time;
    private String clientId;
    private String name;
    private String code;
    private String type;
    private Number level;
    private String provider;
    private String description;

    public Map<String, Object> getVariables() {

        return variables;
    }

    public void setVariables(Map<String, Object> variables) {

        this.variables = variables;
    }

    public String getId() {

        return id;
    }

    public void setId(String id) {

        this.id = id;
    }

    public String getTokenId() {

        return tokenId;
    }

    public void setTokenId(String tokenId) {

        this.tokenId = tokenId;
    }

    public String getUserId() {

        return userId;
    }

    public void setUserId(String userId) {

        this.userId = userId;
    }

    public Date getTime() {

        return time;
    }

    public void setTime(Date time) {

        this.time = time;
    }

    public String getClientId() {

        return clientId;
    }

    public void setClientId(String clientId) {

        this.clientId = clientId;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public String getCode() {

        return code;
    }

    public void setCode(String code) {

        this.code = code;
    }

    public String getType() {

        return type;
    }

    public void setType(String type) {

        this.type = type;
    }

    public Number getLevel() {

        return level;
    }

    public void setLevel(Number level) {

        this.level = level;
    }

    public String getProvider() {

        return provider;
    }

    public void setProvider(String provider) {

        this.provider = provider;
    }

    public String getDescription() {

        return description;
    }

    public void setDescription(String description) {

        this.description = description;
    }

}

package artoria.mail;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("artoria.mail")
public class MailProperties {
    private Boolean enabled;
    private String transportHost;
    private Integer transportPort;
    private Boolean transportBySsl = false;
    private String transportProtocol = "smtp";
    private String storeHost;
    private Integer storePort;
    private Boolean storeBySsl = false;
    private String storeProtocol = "imap";
    private String username;
    private String password;
    private Boolean debug = false;

    public Boolean getEnabled() {

        return enabled;
    }

    public void setEnabled(Boolean enabled) {

        this.enabled = enabled;
    }

    public String getTransportHost() {

        return transportHost;
    }

    public void setTransportHost(String transportHost) {

        this.transportHost = transportHost;
    }

    public Integer getTransportPort() {

        return transportPort;
    }

    public void setTransportPort(Integer transportPort) {

        this.transportPort = transportPort;
    }

    public Boolean getTransportBySsl() {

        return transportBySsl;
    }

    public void setTransportBySsl(Boolean transportBySsl) {

        this.transportBySsl = transportBySsl;
    }

    public String getTransportProtocol() {

        return transportProtocol;
    }

    public void setTransportProtocol(String transportProtocol) {

        this.transportProtocol = transportProtocol;
    }

    public String getStoreHost() {

        return storeHost;
    }

    public void setStoreHost(String storeHost) {

        this.storeHost = storeHost;
    }

    public Integer getStorePort() {

        return storePort;
    }

    public void setStorePort(Integer storePort) {

        this.storePort = storePort;
    }

    public Boolean getStoreBySsl() {

        return storeBySsl;
    }

    public void setStoreBySsl(Boolean storeBySsl) {

        this.storeBySsl = storeBySsl;
    }

    public String getStoreProtocol() {

        return storeProtocol;
    }

    public void setStoreProtocol(String storeProtocol) {

        this.storeProtocol = storeProtocol;
    }

    public String getUsername() {

        return username;
    }

    public void setUsername(String username) {

        this.username = username;
    }

    public String getPassword() {

        return password;
    }

    public void setPassword(String password) {

        this.password = password;
    }

    public Boolean getDebug() {

        return debug;
    }

    public void setDebug(Boolean debug) {

        this.debug = debug;
    }

}

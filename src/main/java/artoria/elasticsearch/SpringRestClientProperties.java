package artoria.elasticsearch;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Spring elasticsearch rest client properties.
 * @author Kahle
 */
@ConfigurationProperties(prefix = "spring.elasticsearch.rest")
public class SpringRestClientProperties {
    /**
     * Comma-separated list of the Elasticsearch instances to use.
     */
    private List<String> uris = new ArrayList<String>(
            Collections.singletonList("http://localhost:9200")
    );
    /**
     * Credentials username.
     */
    private String username;
    /**
     * Credentials password.
     */
    private String password;

    public List<String> getUris() {

        return uris;
    }

    public void setUris(List<String> uris) {

        this.uris = uris;
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

}

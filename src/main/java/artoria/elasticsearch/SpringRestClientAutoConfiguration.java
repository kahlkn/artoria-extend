package artoria.elasticsearch;

import artoria.util.Assert;
import artoria.util.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

import static artoria.common.Constants.ZERO;

/**
 * Spring elasticsearch rest client auto configuration.
 * @author Kahle
 */
@Configuration
@ConditionalOnClass(RestClient.class)
@ConditionalOnMissingClass({"org.springframework.boot.autoconfigure.elasticsearch.rest.RestClientAutoConfiguration"})
@EnableConfigurationProperties(SpringRestClientProperties.class)
public class SpringRestClientAutoConfiguration {
    private static Logger log = LoggerFactory.getLogger(SpringRestClientAutoConfiguration.class);
    private final SpringRestClientProperties springRestClientProperties;

    public SpringRestClientAutoConfiguration(SpringRestClientProperties springRestClientProperties) {
        Assert.notNull(springRestClientProperties, "Parameter \"springRestClientProperties\" must not null. ");
        this.springRestClientProperties = springRestClientProperties;
    }

    @Bean
    @ConditionalOnMissingBean
    public RestClientBuilder restClientBuilder() {
        List<String> uris = springRestClientProperties.getUris();
        String username = springRestClientProperties.getUsername();
        String password = springRestClientProperties.getPassword();
        int size = uris.size();
        HttpHost[] hosts = new HttpHost[size];
        for (int i = ZERO; i < size; i++) {
            String uri = uris.get(i);
            if (StringUtils.isBlank(uri)) {
                continue;
            }
            hosts[i] = HttpHost.create(uri);
        }
        RestClientBuilder restClientBuilder = RestClient.builder(hosts);
        if (StringUtils.isNotBlank(username)) {
            final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            Credentials credentials = new UsernamePasswordCredentials(username, password);
            credentialsProvider.setCredentials(AuthScope.ANY, credentials);
            restClientBuilder.setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
                @Override
                public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpAsyncClientBuilder) {
                    return httpAsyncClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                }
            });
        }
        return restClientBuilder;
    }

    @Bean
    @ConditionalOnMissingBean
    public RestClient restClient(RestClientBuilder restClientBuilder) {

        return restClientBuilder.build();
    }

    @Bean
    @ConditionalOnClass(RestHighLevelClient.class)
    @ConditionalOnMissingBean
    public RestHighLevelClient restHighLevelClient(RestClientBuilder restClientBuilder) {

        return new RestHighLevelClient(restClientBuilder);
    }

}

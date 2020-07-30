package artoria.mail;

import artoria.util.Assert;
import artoria.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Configuration
@ConditionalOnClass({MimeMessage.class})
@ConditionalOnProperty(name = "artoria.mail.enabled", havingValue = "true")
@EnableConfigurationProperties({MailProperties.class})
public class MailAutoConfiguration {
    private static Logger log = LoggerFactory.getLogger(MailAutoConfiguration.class);
    private MailProperties mailProperties;

    @Autowired
    public MailAutoConfiguration(MailProperties mailProperties) {

        this.mailProperties = mailProperties;
    }

    private void fill(Properties prop, String protocol, String host, Integer port, Boolean ssl) {
        Assert.notBlank(protocol, "Parameter \"protocol\" must not blank. ");
        Assert.notNull(prop, "Parameter \"prop\" must not null. ");
        protocol = protocol.toLowerCase();
        if (StringUtils.isBlank(host) || port == null) { return; }
        prop.setProperty("mail." + protocol + ".host", host);
        prop.setProperty("mail." + protocol + ".port", String.valueOf(port));
        if (ssl != null) {
            prop.setProperty("mail." + protocol + ".ssl.enable", String.valueOf(ssl));
        }
    }

    private Properties build(MailProperties mailProperties) {
        Assert.notNull(mailProperties, "Parameter \"mailProperties\" must not null. ");
        Properties properties = new Properties();
        properties.putAll(System.getProperties());
        String transportProtocol = mailProperties.getTransportProtocol();
        if (StringUtils.isNotBlank(transportProtocol)) {
            String transportHost = mailProperties.getTransportHost();
            Integer transportPort = mailProperties.getTransportPort();
            Boolean transportBySsl = mailProperties.getTransportBySsl();
            fill(properties, transportProtocol, transportHost, transportPort, transportBySsl);
            properties.setProperty("mail.transport.protocol", transportProtocol);
        }
        String storeProtocol = mailProperties.getStoreProtocol();
        if (StringUtils.isNotBlank(storeProtocol)) {
            String storeHost = mailProperties.getStoreHost();
            Integer storePort = mailProperties.getStorePort();
            Boolean storeBySsl = mailProperties.getStoreBySsl();
            fill(properties, storeProtocol, storeHost, storePort, storeBySsl);
            properties.setProperty("mail.store.protocol", storeProtocol);
        }
        Boolean debug = mailProperties.getDebug();
        if (debug != null) {
            properties.setProperty("mail.debug", String.valueOf(debug));
        }
        return properties;
    }

    @Bean
    public MailClient mailClient() {
        Properties properties = build(mailProperties);
        String username = mailProperties.getUsername();
        String password = mailProperties.getPassword();
        MailClient mailClient = new JavaMailClient(properties, username, password);
        log.info("The mail client was initialized success. ");
        return mailClient;
    }

}

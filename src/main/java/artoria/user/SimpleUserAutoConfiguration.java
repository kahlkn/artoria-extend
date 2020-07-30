package artoria.user;

import artoria.util.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AutoConfigureBefore({UserAutoConfiguration.class})
@ConditionalOnProperty(name = "artoria.user.enabled", havingValue = "true")
@ConditionalOnMissingBean(name = {"tokenManager", "userManager", "permissionManager"})
@EnableConfigurationProperties({UserProperties.class})
public class SimpleUserAutoConfiguration {
    private static Logger log = LoggerFactory.getLogger(SimpleUserAutoConfiguration.class);
    private final PermissionManager permissionManager;
    private final TokenManager tokenManager;
    private final UserManager userManager;

    @Autowired(required = false)
    public SimpleUserAutoConfiguration(UserProperties userProperties) {

        this(userProperties, null, null);
    }

    @Autowired(required = false)
    public SimpleUserAutoConfiguration(UserProperties userProperties, UserLoader userLoader) {

        this(userProperties, userLoader, null);
    }

    @Autowired(required = false)
    public SimpleUserAutoConfiguration(UserProperties userProperties, UserLoader userLoader, PermissionLoader permissionLoader) {
        Assert.notNull(userProperties, "Parameter \"userProperties\" must not null. ");
        String rolePropertyName = userProperties.getRolePropertyName();
        Long userExpirationTime = userProperties.getUserExpirationTime();
        Long tokenExpirationTime = userProperties.getTokenExpirationTime();
        this.tokenManager = new SimpleTokenManager(tokenExpirationTime);
        this.userManager = new SimpleUserManager(userExpirationTime, userLoader);
        this.permissionManager = new SimplePermissionManager(
                rolePropertyName, userManager, tokenManager, permissionLoader, null);
    }

    @Bean
    @ConditionalOnMissingBean(name = "tokenManager")
    public TokenManager tokenManager() {

        return tokenManager;
    }

    @Bean
    @ConditionalOnMissingBean(name = "userManager")
    public UserManager userManager() {

        return userManager;
    }

    @Bean
    @ConditionalOnMissingBean(name = "permissionManager")
    public PermissionManager permissionManager() {

        return permissionManager;
    }

}

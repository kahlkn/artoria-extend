package artoria.user;

import artoria.util.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
@AutoConfigureAfter({RedisAutoConfiguration.class})
@AutoConfigureBefore({SimpleUserAutoConfiguration.class})
@ConditionalOnClass({RedisOperations.class})
@ConditionalOnProperty(name = "artoria.user.enabled", havingValue = "true")
@ConditionalOnMissingBean(name = {"tokenManager", "userManager", "permissionManager"})
@EnableConfigurationProperties({UserProperties.class})
public class RedisUserAutoConfiguration {
    private static Logger log = LoggerFactory.getLogger(RedisUserAutoConfiguration.class);
    private final PermissionManager permissionManager;
    private final TokenManager tokenManager;
    private final UserManager userManager;

    @Autowired(required = false)
    public RedisUserAutoConfiguration(StringRedisTemplate stringRedisTemplate, UserProperties userProperties) {

        this(stringRedisTemplate, userProperties, null, null);
    }

    @Autowired(required = false)
    public RedisUserAutoConfiguration(StringRedisTemplate stringRedisTemplate, UserProperties userProperties
            , UserLoader userLoader) {

        this(stringRedisTemplate, userProperties, userLoader, null);
    }

    @Autowired(required = false)
    public RedisUserAutoConfiguration(StringRedisTemplate stringRedisTemplate, UserProperties userProperties
            , UserLoader userLoader, PermissionLoader permissionLoader) {
        Assert.notNull(stringRedisTemplate, "Parameter \"stringRedisTemplate\" must not null. ");
        Assert.notNull(userProperties, "Parameter \"userProperties\" must not null. ");
        String rolePropertyName = userProperties.getRolePropertyName();
        Long userExpirationTime = userProperties.getUserExpirationTime();
        Long tokenExpirationTime = userProperties.getTokenExpirationTime();
        this.tokenManager = new RedisTokenManager(stringRedisTemplate, tokenExpirationTime);
        this.userManager = new RedisUserManager(stringRedisTemplate, userExpirationTime, userLoader);
        this.permissionManager = new RedisPermissionManager(stringRedisTemplate,
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

package artoria.user;

import artoria.exception.ExceptionAutoConfiguration;
import artoria.util.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * User auto configuration.
 * @author Kahle
 */
@Configuration
@AutoConfigureAfter({ExceptionAutoConfiguration.class})
@ConditionalOnProperty(name = "artoria.user.enabled", havingValue = "true")
@ConditionalOnBean({PermissionManager.class, TokenManager.class, UserManager.class})
@EnableConfigurationProperties({UserProperties.class})
public class UserAutoConfiguration implements WebMvcConfigurer {
    private static Logger log = LoggerFactory.getLogger(UserAutoConfiguration.class);
    private final UserProperties userProperties;

    @Autowired
    public UserAutoConfiguration(
            PermissionManager permissionManager,
            TokenManager tokenManager,
            UserManager userManager,
            UserProperties userProperties
    ) {
        Assert.notNull(permissionManager, "Parameter \"permissionManager\" must not null. ");
        Assert.notNull(userProperties, "Parameter \"userProperties\" must not null. ");
        Assert.notNull(tokenManager, "Parameter \"tokenManager\" must not null. ");
        Assert.notNull(userManager, "Parameter \"userManager\" must not null. ");
        UserUtils.setPermissionManager(permissionManager);
        UserUtils.setTokenManager(tokenManager);
        UserUtils.setUserManager(userManager);
        this.userProperties = userProperties;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        String tokenHeaderName = userProperties.getTokenHeaderName();
        Assert.notNull(tokenHeaderName, "Variable \"tokenHeaderName\" must not null. ");
        String[] wantExclude = userProperties.getExcludePathPatterns();
        UserInterceptor userInterceptor = new UserInterceptor(tokenHeaderName);
        List<String> willExclude = new ArrayList<String>(Arrays.asList(wantExclude));
        willExclude.add("/error/**");
        registry.addInterceptor(userInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(willExclude);
        log.info("The user tools was initialized success. ");
    }

}

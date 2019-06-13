package artoria.user;

import artoria.config.SwaggerProperties;
import artoria.logging.ControllerLogAutoConfiguration;
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

@Configuration
@EnableConfigurationProperties({UserProperties.class, SwaggerProperties.class})
@AutoConfigureAfter(ControllerLogAutoConfiguration.class)
@ConditionalOnBean({TokenManager.class, UserManager.class})
@ConditionalOnProperty(name = "artoria.user.enabled", havingValue = "true")
public class UserAutoConfiguration implements WebMvcConfigurer {
    private static Logger log = LoggerFactory.getLogger(UserAutoConfiguration.class);
    private final PermissionManager permissionManager;
    private final TokenManager tokenManager;
    private final UserManager userManager;
    private final UserProperties userProperties;
    private final SwaggerProperties swaggerProperties;

    @Autowired
    public UserAutoConfiguration(
            PermissionManager permissionManager,
            TokenManager tokenManager,
            UserManager userManager,
            UserProperties userProperties,
            SwaggerProperties swaggerProperties
    ) {
        this.permissionManager = permissionManager;
        this.tokenManager = tokenManager;
        this.userManager = userManager;
        this.userProperties = userProperties;
        this.swaggerProperties = swaggerProperties;
        UserUtils.setTokenManager(tokenManager);
        UserUtils.setUserManager(userManager);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        String[] wantExclude = userProperties.getExcludePathPatterns();
        UserInterceptor userInterceptor =
                new UserInterceptor(tokenManager, userManager, permissionManager, userProperties);
        List<String> willExclude = new ArrayList<String>(Arrays.asList(wantExclude));
        willExclude.add("/");
        willExclude.add("/error/**");
        Boolean swaggerEnabled = swaggerProperties.getEnabled();
        if (swaggerEnabled != null && swaggerEnabled) {
            willExclude.add("/webjars/springfox-swagger-ui/**");
            willExclude.add("/swagger-resources/**");
            willExclude.add("/swagger-ui.html");
            willExclude.add("/v2/api-docs");
            willExclude.add("/csrf");
        }
        registry.addInterceptor(userInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(willExclude);
        log.info("The user tools was initialized success. ");
    }

}

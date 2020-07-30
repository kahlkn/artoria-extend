package artoria.user;

import org.springframework.boot.context.properties.ConfigurationProperties;

import static artoria.common.Constants.ZERO;

/**
 * User properties.
 * @author Kahle
 */
@ConfigurationProperties("artoria.user")
public class UserProperties {
    /**
     * Enabled user interceptor.
     */
    private Boolean enabled;
    /**
     *
     */
    private String rolePropertyName = "roleCodes";
    /**
     * Token property name.
     */
    private String tokenHeaderName = "Authorization";
    /**
     * Token information expiration time.
     */
    private Long tokenExpirationTime = 7 * 24 * 60 * 60 * 1000L;
    /**
     * User information expiration time.
     */
    private Long userExpirationTime = 14 * 24 * 60 * 60 * 1000L;
    /**
     * URL patterns to which the registered interceptor should not apply to.
     */
    private String[] excludePathPatterns = new String[ZERO];

    public Boolean getEnabled() {

        return enabled;
    }

    public void setEnabled(Boolean enabled) {

        this.enabled = enabled;
    }

    public String getRolePropertyName() {

        return rolePropertyName;
    }

    public void setRolePropertyName(String rolePropertyName) {

        this.rolePropertyName = rolePropertyName;
    }

    public String getTokenHeaderName() {

        return tokenHeaderName;
    }

    public void setTokenHeaderName(String tokenHeaderName) {

        this.tokenHeaderName = tokenHeaderName;
    }

    public Long getTokenExpirationTime() {

        return tokenExpirationTime;
    }

    public void setTokenExpirationTime(Long tokenExpirationTime) {

        this.tokenExpirationTime = tokenExpirationTime;
    }

    public Long getUserExpirationTime() {

        return userExpirationTime;
    }

    public void setUserExpirationTime(Long userExpirationTime) {

        this.userExpirationTime = userExpirationTime;
    }

    public String[] getExcludePathPatterns() {

        return excludePathPatterns;
    }

    public void setExcludePathPatterns(String[] excludePathPatterns) {

        this.excludePathPatterns = excludePathPatterns;
    }

}

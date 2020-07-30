package artoria.user;

import artoria.util.Assert;
import artoria.util.StringUtils;
import artoria.util.ThreadLocalUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User tools.
 * @author Kahle
 */
public class UserUtils {
    private static Logger log = LoggerFactory.getLogger(UserUtils.class);
    /**
     * Thread local token id key.
     */
    private static final String TOKEN_ID_THREAD_LOCAL_KEY = "TOKEN_ID";
    /**
     * Thread local user info key.
     */
    private static final String USER_INFO_THREAD_LOCAL_KEY = "USER_INFO";
    /**
     * Thread local token info key.
     */
    private static final String TOKEN_INFO_THREAD_LOCAL_KEY = "TOKEN_INFO";
    /**
     * Permission manager.
     */
    private static PermissionManager permissionManager;
    /**
     * Token manager.
     */
    private static TokenManager tokenManager;
    /**
     * User manager.
     */
    private static UserManager userManager;

    static void clearThreadLocal() {
        ThreadLocalUtils.remove(TOKEN_ID_THREAD_LOCAL_KEY);
        ThreadLocalUtils.remove(TOKEN_INFO_THREAD_LOCAL_KEY);
        ThreadLocalUtils.remove(USER_INFO_THREAD_LOCAL_KEY);
    }

    public static PermissionManager getPermissionManager() {

        return permissionManager;
    }

    public static void setPermissionManager(PermissionManager permissionManager) {
        Assert.notNull(permissionManager, "Parameter \"permissionManager\" must not null. ");
        log.info("Set permission manager: {}", permissionManager.getClass().getName());
        UserUtils.permissionManager = permissionManager;
    }

    public static TokenManager getTokenManager() {

        return tokenManager;
    }

    public static void setTokenManager(TokenManager tokenManager) {
        Assert.notNull(tokenManager, "Parameter \"tokenManager\" must not null. ");
        log.info("Set token manager: {}", tokenManager.getClass().getName());
        UserUtils.tokenManager = tokenManager;
    }

    public static UserManager getUserManager() {

        return userManager;
    }

    public static void setUserManager(UserManager userManager) {
        Assert.notNull(userManager, "Parameter \"userManager\" must not null. ");
        log.info("Set user manager: {}", userManager.getClass().getName());
        UserUtils.userManager = userManager;
    }

    public static String getTokenId() {

        return (String) ThreadLocalUtils.getValue(TOKEN_ID_THREAD_LOCAL_KEY);
    }

    public static void setTokenId(String tokenId) {

        ThreadLocalUtils.setValue(TOKEN_ID_THREAD_LOCAL_KEY, tokenId);
    }

    public static Token getToken() {
        Token token = (Token) ThreadLocalUtils.getValue(TOKEN_INFO_THREAD_LOCAL_KEY);
        if (token != null) { return token; }
        String tokenId = UserUtils.getTokenId();
        if (StringUtils.isBlank(tokenId)) { return null; }
        token = getTokenManager().findById(tokenId);
        if (token == null) { return null; }
        ThreadLocalUtils.setValue(TOKEN_INFO_THREAD_LOCAL_KEY, token);
        return token;
    }

    public static UserInfo getUserInfo() {
        UserInfo userInfo = (UserInfo) ThreadLocalUtils.getValue(USER_INFO_THREAD_LOCAL_KEY);
        if (userInfo != null) { return userInfo; }
        Token token = UserUtils.getToken();
        if (token == null) { return null; }
        String userId = token.getUserId();
        if (StringUtils.isBlank(userId)) { return null; }
        userInfo = getUserManager().findById(userId);
        if (userInfo == null) { return null; }
        ThreadLocalUtils.setValue(USER_INFO_THREAD_LOCAL_KEY, userInfo);
        return userInfo;
    }

}

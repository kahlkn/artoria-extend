package artoria.user;

import artoria.exception.VerifyUtils;
import artoria.util.StringUtils;
import artoria.util.ThreadLocalUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static artoria.common.DefaultErrorCode.PARAMETER_IS_REQUIRED;

/**
 * User tools.
 * @author Kahle
 */
public class UserUtils {
    private static Logger log = LoggerFactory.getLogger(UserUtils.class);
    /**
     * ThreadLocal tokenId key
     */
    public static final String TOKEN_ID_THREAD_LOCAL_KEY = "TOKEN_ID";
    /**
     * ThreadLocal token key
     */
    public static final String TOKEN_THREAD_LOCAL_KEY = "TOKEN";
    /**
     * ThreadLocal userInfo key
     */
    public static final String USER_INFO_THREAD_LOCAL_KEY = "USER_INFO";
    /**
     * Token manager.
     */
    private static TokenManager tokenManager;
    /**
     * User manager.
     */
    private static UserManager userManager;

    public static TokenManager getTokenManager() {

        return tokenManager;
    }

    public static void setTokenManager(TokenManager tokenManager) {
        VerifyUtils.notNull(tokenManager, PARAMETER_IS_REQUIRED);
        log.info("Set token manager: {}", tokenManager.getClass().getName());
        UserUtils.tokenManager = tokenManager;
    }

    public static UserManager getUserManager() {

        return userManager;
    }

    public static void setUserManager(UserManager userManager) {
        VerifyUtils.notNull(userManager, PARAMETER_IS_REQUIRED);
        log.info("Set user manager: {}", tokenManager.getClass().getName());
        UserUtils.userManager = userManager;
    }

    public static String getTokenId() {

        return (String) ThreadLocalUtils.getValue(TOKEN_ID_THREAD_LOCAL_KEY);
    }

    public static void setTokenId(String tokenId) {

        ThreadLocalUtils.setValue(TOKEN_ID_THREAD_LOCAL_KEY, tokenId);
    }

    public static Token getToken() {
        Token token = (Token) ThreadLocalUtils.getValue(TOKEN_THREAD_LOCAL_KEY);
        if (token != null) { return token; }
        String tokenId = UserUtils.getTokenId();
        if (StringUtils.isBlank(tokenId)) { return null; }
        token = getTokenManager().find(tokenId);
        if (token == null) { return null; }
        ThreadLocalUtils.setValue(TOKEN_THREAD_LOCAL_KEY, token);
        return token;
    }

    public static UserInfo getUserInfo() {
        UserInfo userInfo = (UserInfo) ThreadLocalUtils.getValue(USER_INFO_THREAD_LOCAL_KEY);
        if (userInfo != null) { return userInfo; }
        Token token = UserUtils.getToken();
        if (token == null) { return null; }
        String userId = token.getUserId();
        if (StringUtils.isBlank(userId)) { return null; }
        userInfo = getUserManager().find(userId);
        if (userInfo == null) { return null; }
        ThreadLocalUtils.setValue(USER_INFO_THREAD_LOCAL_KEY, userInfo);
        return userInfo;
    }

}

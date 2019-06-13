package artoria.user;

import artoria.exception.BusinessException;
import artoria.exception.VerifyUtils;
import artoria.servlet.RequestUtils;
import artoria.util.ThreadLocalUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static artoria.common.DefaultErrorCode.*;
import static artoria.user.UserUtils.*;

/**
 * User interceptor.
 * @author Kahle
 */
public class UserInterceptor extends HandlerInterceptorAdapter {
    private static final String OPTIONS_METHOD = "OPTIONS";
    private PermissionManager permissionManager;
    private TokenManager tokenManager;
    private UserManager userManager;
    private UserProperties userProperties;

    public UserInterceptor(
            TokenManager tokenManager,
            UserManager userManager,
            PermissionManager permissionManager,
            UserProperties userProperties
    ) {
        this.tokenManager = tokenManager;
        this.userManager = userManager;
        this.permissionManager = permissionManager;
        String tokenName = userProperties.getTokenName();
        VerifyUtils.notBlank(tokenName, PARAMETER_IS_REQUIRED);
        this.userProperties = userProperties;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String method = request.getMethod();
        if (OPTIONS_METHOD.equals(method)) { return true; }
        String tokenName = userProperties.getTokenName();
        String tokenId = request.getHeader(tokenName);
        UserUtils.setTokenId(tokenId);

        String requestURI = request.getRequestURI();
        boolean auth =
                permissionManager.authenticate(requestURI, (String) null);
        if (auth) { return true; }

        VerifyUtils.notBlank(tokenId, NO_LOGIN);
        Token token = UserUtils.getToken();
        VerifyUtils.notNull(token, INVALID_TOKEN);
        token.setLastAccessedTime(System.currentTimeMillis());
        token.setLastAccessedAddress(RequestUtils.getRemoteAddr(request));
        tokenManager.save(token);
        userManager.refresh(token.getUserId());
        tokenManager.refresh(tokenId);

        auth = permissionManager.authenticate(requestURI, token);
        if (!auth) { throw new BusinessException(NO_PERMISSION); }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        ThreadLocalUtils.remove(TOKEN_ID_THREAD_LOCAL_KEY);
        ThreadLocalUtils.remove(TOKEN_THREAD_LOCAL_KEY);
        ThreadLocalUtils.remove(USER_INFO_THREAD_LOCAL_KEY);
    }

}

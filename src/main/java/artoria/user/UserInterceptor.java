package artoria.user;

import artoria.exception.BusinessException;
import artoria.servlet.RequestUtils;
import artoria.util.Assert;
import artoria.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static artoria.common.InternalErrorCode.*;

/**
 * User interceptor.
 * @author Kahle
 */
public class UserInterceptor extends HandlerInterceptorAdapter {
    private static Logger log = LoggerFactory.getLogger(UserInterceptor.class);
    private static final String OPTIONS_METHOD = "OPTIONS";
    private final PermissionManager permissionManager;
    private final TokenManager tokenManager;
    private final UserManager userManager;
    private final String tokenHeaderName;

    public UserInterceptor(String tokenHeaderName) {
        Assert.notBlank(tokenHeaderName, "Parameter \"tokenHeaderName\" must not blank. ");
        this.permissionManager = UserUtils.getPermissionManager();
        this.tokenManager = UserUtils.getTokenManager();
        this.userManager = UserUtils.getUserManager();
        this.tokenHeaderName = tokenHeaderName;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            String method = request.getMethod();
            if (OPTIONS_METHOD.equals(method)) { return true; }
            String tokenId = request.getHeader(tokenHeaderName);
            if (StringUtils.isBlank(tokenId)) {
                tokenId = request.getParameter(tokenHeaderName);
            }
            UserUtils.setTokenId(tokenId);

            String requestURI = request.getRequestURI();
            boolean auth =
                    permissionManager.authenticate(requestURI, (List<String>) null);
            if (auth) { return true; }
            if (StringUtils.isBlank(tokenId)) {
                log.info("The token ID is blank when accessing \"{}\". ", requestURI);
                throw new BusinessException(NO_LOGIN);
            }
            Token token = UserUtils.getToken();
            if (token == null) {
                log.info("This token ID is invalid and its content is \"{}\". ", tokenId);
                throw new BusinessException(INVALID_TOKEN);
            }

            token.setAccessTime(String.valueOf(System.currentTimeMillis()));
            token.setClientNetAddress(RequestUtils.getRemoteAddress(request));
            String userId = token.getUserId();
            tokenManager.save(token);
            userManager.refresh(userId);
            tokenManager.refresh(tokenId);

            auth = permissionManager.authenticate(requestURI, token);
            if (!auth) { throw new BusinessException(NO_PERMISSION); }
            return true;
        }
        catch (Exception e) {
            UserUtils.clearThreadLocal();
            throw e;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

        UserUtils.clearThreadLocal();
    }

}

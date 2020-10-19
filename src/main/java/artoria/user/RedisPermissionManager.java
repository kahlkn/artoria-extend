package artoria.user;

import artoria.thread.SimpleThreadFactory;
import artoria.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.*;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;

import static artoria.common.Constants.*;
import static artoria.util.ObjectUtils.cast;
import static java.lang.Boolean.TRUE;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * Permission manager impl by redis.
 * @author Kahle
 */
public class RedisPermissionManager implements PermissionManager {
    private static final String THREAD_NAME_PREFIX = "redis-permission-manager-executor";
    private static final String PERMISSION_KEY = "PERMISSION";
    private static final String EXEMPT_LOGIN = "exempt_login";
    private static final long DEFAULT_INITIAL_DELAY = 4000L;
    private static final long DEFAULT_RELOAD_PERIOD = 60000L;
    private static Logger log = LoggerFactory.getLogger(RedisPermissionManager.class);
    private final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;
    private final RedisTemplate<String, String> strStrRedisTemplate;
    private final PermissionLoader permissionLoader;
    private final TokenManager tokenManager;
    private final UserManager userManager;
    private final String rolePropertyName;

    public RedisPermissionManager(RedisTemplate<String, String> strStrRedisTemplate,
                                  String rolePropertyName,
                                  UserManager userManager,
                                  TokenManager tokenManager,
                                  PermissionLoader permissionLoader,
                                  Long reloadPeriod) {
        Assert.notNull(strStrRedisTemplate, "Parameter \"strStrRedisTemplate\" must not null. ");
        Assert.notBlank(rolePropertyName, "Parameter \"rolePropertyName\" must not blank. ");
        Assert.notNull(tokenManager, "Parameter \"tokenManager\" must not null. ");
        Assert.notNull(userManager, "Parameter \"userManager\" must not null. ");
        this.strStrRedisTemplate = strStrRedisTemplate;
        this.rolePropertyName = rolePropertyName;
        this.userManager = userManager;
        this.tokenManager = tokenManager;
        this.permissionLoader = permissionLoader;
        if (permissionLoader != null) {
            if (reloadPeriod == null || reloadPeriod <= ZERO) { reloadPeriod = DEFAULT_RELOAD_PERIOD; }
            ThreadFactory threadFactory = new SimpleThreadFactory(THREAD_NAME_PREFIX, TRUE);
            PermissionLoadRunnable runnable = new PermissionLoadRunnable();
            this.scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(ONE, threadFactory);
            scheduledThreadPoolExecutor.scheduleAtFixedRate(
                    runnable, DEFAULT_INITIAL_DELAY, reloadPeriod, MILLISECONDS
            );
            ShutdownHookUtils.addExecutorService(scheduledThreadPoolExecutor);
        }
        else { this.scheduledThreadPoolExecutor = null; }
    }

    private class PermissionLoadRunnable implements Runnable {
        @Override
        public void run() {
            try {
                Map<String, String> stringMap = cast(permissionLoader.load());
                if (MapUtils.isEmpty(stringMap)) {
                    log.info("The permission loader didn't load anything. ");
                    return;
                }
                HashOperations<String, Object, Object> opsForHash = strStrRedisTemplate.opsForHash();
                Map<Object, Object> entries = opsForHash.entries(PERMISSION_KEY);
                if (MapUtils.isNotEmpty(entries)) {
                    Set<Object> keySet = entries.keySet();
                    keySet.removeAll(stringMap.keySet());
                    if (CollectionUtils.isNotEmpty(keySet)) {
                        opsForHash.delete(PERMISSION_KEY, keySet.toArray());
                    }
                }
                opsForHash.putAll(PERMISSION_KEY, stringMap);
                log.info("The permission loader load success. ");
            }
            catch (Exception e) {
                log.info("The permission loader load failure. ", e);
            }
        }
    }

    @Override
    public void save(String resource, Collection<String> roleCodes) {
        Assert.notBlank(resource, "Parameter \"resource\" must not blank. ");
        Assert.notEmpty(roleCodes, "Parameter \"roleCodes\" must not empty. ");
        HashOperations<String, Object, Object> opsForHash = strStrRedisTemplate.opsForHash();
        StringBuilder stringBuilder = new StringBuilder();
        for (String roleCode : roleCodes) {
            stringBuilder.append(roleCode);
            stringBuilder.append(COMMA);
        }
        int length = stringBuilder.length();
        if (stringBuilder.length() > ZERO) {
            stringBuilder.deleteCharAt(length - ONE);
        }
        String roleCodesStr = stringBuilder.toString();
        opsForHash.put(PERMISSION_KEY, resource, roleCodesStr);
    }

    @Override
    public void remove(String resource, Collection<String> roleCodes) {
        Assert.notBlank(resource, "Parameter \"resource\" must not blank. ");
        Assert.notEmpty(roleCodes, "Parameter \"roleCodes\" must not empty. ");
        HashOperations<String, Object, Object> opsForHash = strStrRedisTemplate.opsForHash();
        Collection<String> oldRoleCodes = findByResource(resource);
        if (oldRoleCodes.removeAll(roleCodes)
                && CollectionUtils.isNotEmpty(oldRoleCodes)) {
            save(resource, oldRoleCodes);
        }
        else { opsForHash.delete(PERMISSION_KEY, resource); }
    }

    @Override
    public void clear() {

        strStrRedisTemplate.delete(PERMISSION_KEY);
    }

    @Override
    public Collection<String> findByResource(String resource) {
        Assert.notBlank(resource, "Parameter \"resource\" must not blank. ");
        HashOperations<String, Object, Object> opsForHash = strStrRedisTemplate.opsForHash();
        String roleCodesStr = (String) opsForHash.get(PERMISSION_KEY, resource);
        if (StringUtils.isBlank(roleCodesStr)) { return null; }
        return new ArrayList<String>(Arrays.asList(roleCodesStr.split(COMMA)));
    }

    @Override
    public boolean authenticate(String resource, Token token) {
        Assert.notBlank(resource, "Parameter \"resource\" must not blank. ");
        if (token == null) { return authenticate(resource, (List<String>) null); }
        String userId = token.getUserId();
        if (StringUtils.isBlank(userId)) {
            String tokenId = token.getId();
            Assert.notBlank(tokenId, "Variable \"tokenId\" must not blank. ");
            token = tokenManager.findById(tokenId);
            Assert.notNull(token, "Variable \"token\" must not null. ");
            userId = token.getUserId();
            Assert.notBlank(userId, "Variable \"userId\" must not blank. ");
        }
        UserInfo userInfo = userManager.findById(userId);
        return authenticate(resource, userInfo);
    }

    @Override
    public boolean authenticate(String resource, UserInfo userInfo) {
        Assert.notBlank(resource, "Parameter \"resource\" must not blank. ");
        if (userInfo == null) { return authenticate(resource, (List<String>) null); }
        Object roleCodesObj = userInfo.get(rolePropertyName);
        if (roleCodesObj == null) {
            String userId = userInfo.getId();
            Assert.notBlank(userId, "Variable \"userId\" must not blank. ");
            userInfo = userManager.findById(userId);
            Assert.notNull(userInfo, "Variable \"userInfo\" must not null. ");
            roleCodesObj = userInfo.get(rolePropertyName);
        }
        List<String> roleCodes;
        if (roleCodesObj instanceof List) {
            roleCodes = ObjectUtils.cast(roleCodesObj);
        }
        else {
            if (roleCodesObj != null) {
                String roleCodesStr = String.valueOf(roleCodesObj);
                String[] split = roleCodesStr.split(COMMA);
                roleCodes = Arrays.asList(split);
            }
            else {
                roleCodes = null;
            }
        }
        return authenticate(resource, roleCodes);
    }

    @Override
    public boolean authenticate(String resource, Collection<String> roleCodes) {
        Collection<String> resRoleCodes = findByResource(resource);
        if (CollectionUtils.isEmpty(roleCodes)) {
            // When "resRoleCodes" is not empty, it means that only judge exempt login.
            // When "resRoleCodes" is empty, it means the resource is not configured, so return false(Default need login).
            return StringUtils.isNotEmpty(resRoleCodes) && resRoleCodes.contains(EXEMPT_LOGIN);
        }
        else {
            // When "resRoleCodes" is empty, return true because it is already logged in.
            // When "resRoleCodes" is not empty, determine if there is "roleCodes" in "resRoleCodes".
            if (CollectionUtils.isEmpty(resRoleCodes)) { return true; }
            List<String> list = new ArrayList<String>(roleCodes);
            list.retainAll(resRoleCodes);
            return list.size() > ZERO;
        }
    }

    @Override
    protected void finalize() {
        if (scheduledThreadPoolExecutor != null
                && !scheduledThreadPoolExecutor.isShutdown()) {
            scheduledThreadPoolExecutor.shutdown();
        }
    }

}

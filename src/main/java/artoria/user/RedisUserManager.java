package artoria.user;

import artoria.util.Assert;
import artoria.util.CollectionUtils;
import artoria.util.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static artoria.common.Constants.ASTERISK;
import static artoria.common.Constants.ZERO;
import static artoria.util.ObjectUtils.cast;

/**
 * User manager impl by redis.
 * @author Kahle
 */
public class RedisUserManager implements UserManager {
    private static final String USER_INFO_PREFIX = "USER_INFO:";
    private static Logger log = LoggerFactory.getLogger(RedisUserManager.class);
    private final StringRedisTemplate stringRedisTemplate;
    private final UserLoader userLoader;
    private final Long userExpirationTime;

    public RedisUserManager(StringRedisTemplate stringRedisTemplate, Long userExpirationTime) {

        this(stringRedisTemplate, userExpirationTime, null);
    }

    public RedisUserManager(StringRedisTemplate stringRedisTemplate, Long userExpirationTime, UserLoader userLoader) {
        Assert.notNull(stringRedisTemplate, "Parameter \"stringRedisTemplate\" must not null. ");
        Assert.notNull(userExpirationTime, "Parameter \"userExpirationTime\" must not null. ");
        if (userExpirationTime <= ZERO) { userExpirationTime = -1L; }
        this.stringRedisTemplate = stringRedisTemplate;
        this.userExpirationTime = userExpirationTime;
        this.userLoader = userLoader;
    }

    public Long getUserExpirationTime() {

        return userExpirationTime;
    }

    @Override
    public void save(UserInfo userInfo) {
        Assert.notNull(userInfo, "Parameter \"userInfo\" must not null. ");
        String userId = userInfo.getId();
        Assert.notBlank(userId, "Parameter \"userId\" must not blank. ");
        String redisKey = USER_INFO_PREFIX + userId;
        HashOperations<String, Object, Object> opsForHash = stringRedisTemplate.opsForHash();
        opsForHash.putAll(redisKey, userInfo.toMap());
        if (userExpirationTime <= ZERO) { return; }
        stringRedisTemplate.expire(redisKey, userExpirationTime, TimeUnit.MILLISECONDS);
    }

    @Override
    public void refresh(String userId) {
        Assert.notBlank(userId, "Parameter \"userId\" must not blank. ");
        if (userExpirationTime <= ZERO) { return; }
        String redisKey = USER_INFO_PREFIX + userId;
        Boolean hasKey = stringRedisTemplate.hasKey(redisKey);
        if (hasKey == null || !hasKey) { return; }
        stringRedisTemplate.expire(redisKey, userExpirationTime, TimeUnit.MILLISECONDS);
    }

    @Override
    public void remove(String userId) {
        Assert.notBlank(userId, "Parameter \"userId\" must not blank. ");
        String redisKey = USER_INFO_PREFIX + userId;
        stringRedisTemplate.delete(redisKey);
    }

    @Override
    public void clear() {
        final String pattern = USER_INFO_PREFIX + ASTERISK;
        Set<String> keys = stringRedisTemplate.keys(pattern);
        if (CollectionUtils.isEmpty(keys)) { return; }
        stringRedisTemplate.delete(keys);
//        stringRedisTemplate.execute(new RedisCallback<Object>() {
//            @Override
//            public Object doInRedis(@Nullable RedisConnection connection) throws DataAccessException {
//                if (connection == null) {
//                    throw new IllegalArgumentException("RedisConnection is null. ");
//                }
//                Cursor<byte[]> cursor = null;
//                try {
//                    ScanOptions scanOptions = ScanOptions
//                            .scanOptions().count(Long.MAX_VALUE).match(pattern).build();
//                    cursor = connection.scan(scanOptions);
//                    while (cursor.hasNext()) {
//                        byte[] next = cursor.next();
//                        //
//                    }
//                }
//                finally {
//                    CloseUtils.closeQuietly(cursor);
//                }
//                return null;
//            }
//        });
    }

    @Override
    public UserInfo findById(String userId) {
        Assert.notBlank(userId, "Parameter \"userId\" must not blank. ");
        String redisKey = USER_INFO_PREFIX + userId;
        Boolean hasKey = stringRedisTemplate.hasKey(redisKey);
        if (hasKey != null && hasKey) {
            HashOperations<String, Object, Object> opsForHash = stringRedisTemplate.opsForHash();
            Map<String, Object> entries = cast(opsForHash.entries(redisKey));
            if (MapUtils.isEmpty(entries)) { return null; }
            UserInfo userInfo = new UserInfo();
            userInfo.fromMap(entries);
            return userInfo;
        }
        else {
            if (userLoader == null) { return null; }
            UserInfo userInfo = userLoader.load(userId);
            if (userInfo == null) { return null; }
            save(userInfo);
            return userInfo;
        }
    }

}

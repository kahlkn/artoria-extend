package artoria.user;

import artoria.identifier.IdentifierUtils;
import artoria.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static artoria.common.Constants.*;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * Token manager impl by redis.
 * @author Kahle
 */
public class RedisTokenManager implements TokenManager {
    private static final String TOKEN_PREFIX = "TOKEN:";
    private static final String ASTERISK_AT = "*@";
    private static Logger log = LoggerFactory.getLogger(RedisTokenManager.class);
    private final StringRedisTemplate stringRedisTemplate;
    private final Long tokenExpirationTime;

    public RedisTokenManager(StringRedisTemplate stringRedisTemplate, Long tokenExpirationTime) {
        Assert.notNull(stringRedisTemplate, "Parameter \"stringRedisTemplate\" must not null. ");
        Assert.notNull(tokenExpirationTime, "Parameter \"tokenExpirationTime\" must not null. ");
        if (tokenExpirationTime <= ZERO) { tokenExpirationTime = -1L; }
        this.tokenExpirationTime = tokenExpirationTime;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public void save(Token token) {
        Assert.notNull(token, "Parameter \"token\" must not null. ");
        String userId = token.getUserId();
        Assert.notBlank(userId, "Parameter \"userId\" must not blank. ");
        String tokenId = token.getId();
        if (StringUtils.isBlank(tokenId)) {
            token.setId(tokenId = generateId());
        }
        String redisKey = TOKEN_PREFIX + userId + AT_SIGN + tokenId;
        HashOperations<String, Object, Object> opsForHash = stringRedisTemplate.opsForHash();
        opsForHash.putAll(redisKey, token.toMap());
        if (tokenExpirationTime <= ZERO) { return; }
        stringRedisTemplate.expire(redisKey, tokenExpirationTime, MILLISECONDS);
    }

    @Override
    public void refresh(String tokenId) {
        Assert.notBlank(tokenId, "Parameter \"tokenId\" must not blank. ");
        if (tokenExpirationTime <= ZERO) { return; }
        String pattern = TOKEN_PREFIX + ASTERISK_AT + tokenId;
        Set<String> keys = stringRedisTemplate.keys(pattern);
        if (CollectionUtils.isEmpty(keys)) { return; }
        String key = keys.iterator().next();
        stringRedisTemplate.expire(key, tokenExpirationTime, MILLISECONDS);
    }

    @Override
    public void remove(String tokenId) {
        Assert.notBlank(tokenId, "Parameter \"tokenId\" must not blank. ");
        String pattern = TOKEN_PREFIX + ASTERISK_AT + tokenId;
        Set<String> keys = stringRedisTemplate.keys(pattern);
        if (CollectionUtils.isEmpty(keys)) { return; }
        stringRedisTemplate.delete(keys.iterator().next());
    }

    @Override
    public void clear() {
        String pattern = TOKEN_PREFIX + ASTERISK;
        Set<String> keys = stringRedisTemplate.keys(pattern);
        if (CollectionUtils.isEmpty(keys)) { return; }
        stringRedisTemplate.delete(keys);
    }

    @Override
    public void removeByUserId(String userId) {
        Assert.notBlank(userId, "Parameter \"userId\" must not blank. ");
        String pattern = TOKEN_PREFIX + userId + ASTERISK;
        Set<String> keys = stringRedisTemplate.keys(pattern);
        if (CollectionUtils.isEmpty(keys)) { return; }
        stringRedisTemplate.delete(keys);
    }

    @Override
    public String generateId() {

        return IdentifierUtils.nextStringIdentifier();
    }

    @Override
    public Token findById(String tokenId) {
        Assert.notBlank(tokenId, "Parameter \"tokenId\" must not blank. ");
        String pattern = TOKEN_PREFIX + ASTERISK_AT + tokenId;
        Set<String> keys = stringRedisTemplate.keys(pattern);
        if (CollectionUtils.isEmpty(keys)) { return null; }
        HashOperations<String, Object, Object> opsForHash = stringRedisTemplate.opsForHash();
        String key = keys.iterator().next();
        Map<String, Object> entries = ObjectUtils.cast(opsForHash.entries(key));
        if (MapUtils.isEmpty(entries)) { return null; }
        Token token = new Token();
        token.fromMap(entries);
        return token;
    }

    @Override
    public List<Token> findByUserId(String userId) {
        Assert.notBlank(userId, "Parameter \"userId\" must not blank. ");
        throw new UnsupportedOperationException();
    }

}

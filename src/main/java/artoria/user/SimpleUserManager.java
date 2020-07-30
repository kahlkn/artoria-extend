package artoria.user;

import artoria.cache.Cache;
import artoria.cache.SimpleCache;
import artoria.collection.ReferenceMap;
import artoria.util.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static artoria.common.Constants.ZERO;
import static artoria.util.ObjectUtils.cast;

public class SimpleUserManager implements UserManager {
    private static Logger log = LoggerFactory.getLogger(SimpleUserManager.class);
    private final UserLoader userLoader;
    private final Cache cache;
    private final Long userExpirationTime;

    public SimpleUserManager(Long userExpirationTime) {

        this(userExpirationTime, null);
    }

    public SimpleUserManager(Long userExpirationTime, UserLoader userLoader) {
        Assert.notNull(userExpirationTime, "Parameter \"userExpirationTime\" must not null. ");
        if (userExpirationTime < ZERO) { userExpirationTime = 0L; }
        this.userExpirationTime = userExpirationTime;
        this.userLoader = userLoader;
        this.cache = new SimpleCache(
                getClass().getName(), ZERO, userExpirationTime, ReferenceMap.Type.SOFT
        );
    }

    public Long getUserExpirationTime() {

        return userExpirationTime;
    }

    @Override
    public void save(UserInfo userInfo) {
        Assert.notNull(userInfo, "Parameter \"userInfo\" must not null. ");
        String userId = userInfo.getId();
        Assert.notBlank(userId, "Parameter \"userId\" must not blank. ");
        cache.put(userId, userInfo);
    }

    @Override
    public void refresh(String userId) {
        Assert.notBlank(userId, "Parameter \"userId\" must not blank. ");
        if (userExpirationTime <= ZERO) { return; }
        cache.get(userId);
    }

    @Override
    public void remove(String userId) {
        Assert.notBlank(userId, "Parameter \"userId\" must not blank. ");
        cache.remove(userId);
    }

    @Override
    public void clear() {

        cache.clear();
    }

    @Override
    public UserInfo findById(String userId) {
        Assert.notBlank(userId, "Parameter \"userId\" must not blank. ");
        Object object = cache.get(userId);
        if (object != null) {
            return cast(object);
        }
        else {
            if (userLoader == null) { return null; }
            UserInfo userInfo = userLoader.load(userId);
            save(userInfo);
            return userInfo;
        }
    }

}

package artoria.identifier;

import artoria.util.Assert;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

import static artoria.common.Constants.ONE;
import static artoria.common.Constants.ZERO;
import static artoria.time.DateUtils.create;

/**
 * Redis increment identifier generator.
 * @see <a href="https://redis.io/commands/incrby">INCRBY key increment</a>
 * @author Kahle
 */
public class RedisIncrIdGenerator extends AbstractIncrIdGenerator {
    private final long timeOffset = create(2020, ONE, ONE, ZERO, ZERO, ZERO, ZERO).getTimeInMillis();
    private final StringRedisTemplate stringRedisTemplate;

    public RedisIncrIdGenerator(StringRedisTemplate stringRedisTemplate) {
        Assert.notNull(stringRedisTemplate, "Parameter \"stringRedisTemplate\" must not null. ");
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    protected Long incrementAndGet() {
        int stepLength = getStepLength();
        String name = getName();
        ValueOperations<String, String> opsForValue = stringRedisTemplate.opsForValue();
        Long increment = opsForValue.increment(name, stepLength);
        if (increment == null) {
            throw new IllegalStateException("An error is likely due to use pipeline / transaction. ");
        }
        // Set expire time.
        TimeUnit expireTimeUnit = getExpireTimeUnit();
        long expireTime = getExpireTime();
        if (increment == stepLength && expireTime > ZERO) {
            long currentTimeMillis = System.currentTimeMillis();
            currentTimeMillis = currentTimeMillis - timeOffset;
            expireTime = expireTime - currentTimeMillis % expireTime;
            stringRedisTemplate.expire(name, expireTime, expireTimeUnit);
        }
        return increment;
    }

}

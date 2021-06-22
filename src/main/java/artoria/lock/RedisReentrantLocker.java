package artoria.lock;

import artoria.collect.ReferenceMap;
import artoria.lang.ReferenceType;
import artoria.util.Assert;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class RedisReentrantLocker implements Locker {
    private static Logger log = LoggerFactory.getLogger(RedisReentrantLocker.class);
    private final ReentrantLock CREATION_LOCK = new ReentrantLock();
    private Map<String, Lock> storage;
    private RedissonClient redisson;

    public RedisReentrantLocker(RedissonClient redisson) {
        this.redisson = redisson;
        ReferenceType type = ReferenceType.SOFT;
        this.storage = new ReferenceMap<String, Lock>(type,
                new ConcurrentHashMap<String, ReferenceMap.ValueCell<String, Lock>>()
        );
    }

    public RedisReentrantLocker(RedissonClient redisson, Map<String, Lock> storage) {
        Assert.notNull(redisson, "Parameter \"redisson\" must not null. ");
        Assert.notNull(storage, "Parameter \"storage\" must not null. ");
        this.redisson = redisson;
        this.storage = storage;
    }

    private Lock getLock(String lockName) {
        Assert.notBlank(lockName, "Parameter \"lockName\" must not blank. ");
        Lock lock = storage.get(lockName);
        if (lock != null) { return lock; }
        CREATION_LOCK.lock();
        try {
            lock = storage.get(lockName);
            if (lock != null) { return lock; }
            lock = redisson.getLock(lockName);
            storage.put(lockName, lock);
            return lock;
        }
        finally {
            CREATION_LOCK.unlock();
        }
    }

    @Override
    public void lock(String lockName) {

        this.getLock(lockName).lock();
    }

    @Override
    public void unlock(String lockName) {

        this.getLock(lockName).unlock();
    }

    @Override
    public void lockInterruptibly(String lockName) throws InterruptedException {

        this.getLock(lockName).lockInterruptibly();
    }

    @Override
    public boolean tryLock(String lockName) {

        return this.getLock(lockName).tryLock();
    }

    @Override
    public boolean tryLock(String lockName, long time, TimeUnit unit) throws InterruptedException {

        return this.getLock(lockName).tryLock(time, unit);
    }

}

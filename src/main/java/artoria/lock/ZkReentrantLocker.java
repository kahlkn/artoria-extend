package artoria.lock;

import artoria.exception.ExceptionUtils;
import artoria.logging.Logger;
import artoria.logging.LoggerFactory;
import artoria.util.Assert;
import artoria.zookeeper.ZkConnectionStateListener;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.concurrent.TimeUnit;

import static artoria.common.Constants.SLASH;
import static artoria.common.Constants.ZERO;

/**
 * Zookeeper reentrant mutex lock operator.
 * @see org.apache.curator.framework.recipes.locks.InterProcessMutex
 * @see artoria.lock.Locker
 * @author Kahle
 */
public class ZkReentrantLocker implements Locker {
    private static Logger log = LoggerFactory.getLogger(ZkReentrantLocker.class);
    private static final String REENTRANT_LOCK = "/lock/mutex/reentrant";
    private final CuratorFramework curator;
    private TimeUnit zkTimeUnit = TimeUnit.MILLISECONDS;
    private long zkTimeout = 500;

    public ZkReentrantLocker(String connectString) {
        Assert.notBlank(connectString, "Parameter \"connectString\" must not blank. ");
        this.curator = CuratorFrameworkFactory.builder()
                .retryPolicy(new ExponentialBackoffRetry(1000, Integer.MAX_VALUE))
                .sessionTimeoutMs(30000)
                .connectionTimeoutMs(30000)
                .connectString(connectString)
                .build();
        ConnectionStateListener listener = new ZkConnectionStateListener("zk-" + connectString);
        this.curator.getConnectionStateListenable().addListener(listener);
        this.curator.start();
        log.info("Initialize ZkReentrantLocker success, and zookeeper connect address is \"" + connectString + "\". ");
    }

    public ZkReentrantLocker(CuratorFramework curator) {
        Assert.notNull(curator, "Parameter \"curator\" must not null. ");
        this.curator = curator;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        if (curator == null) { return; }
        curator.close();
    }

    public long getZkTimeout() {

        return this.zkTimeout;
    }

    public TimeUnit getZkTimeUnit() {

        return this.zkTimeUnit;
    }

    public void setZkTimeout(long zkTimeout, TimeUnit zkTimeUnit) {
        Assert.isTrue(zkTimeout > ZERO, "Parameter \"zkTimeout\" must greater than 0. ");
        Assert.notNull(zkTimeUnit, "Parameter \"zkTimeUnit\" must not null. ");
        this.zkTimeout = zkTimeout;
        this.zkTimeUnit = zkTimeUnit;
    }

    private InterProcessMutex getMutexLock(String lockName) {
        Assert.notBlank(lockName, "Parameter \"lockName\" must not blank. ");
        String path = REENTRANT_LOCK + SLASH + lockName;
        return new InterProcessMutex(this.curator, path);
    }

    @Override
    public void lock(String lockName) {
        try {
            getMutexLock(lockName).acquire();
        }
        catch (Exception e) {
            throw ExceptionUtils.wrap(e);
        }
    }

    @Override
    public void unlock(String lockName) {
        try {
            getMutexLock(lockName).release();
        }
        catch (Exception e) {
            throw ExceptionUtils.wrap(e);
        }
    }

    @Override
    public void lockInterruptibly(String lockName) {

        throw new UnsupportedOperationException();
    }

    @Override
    public boolean tryLock(String lockName) {
        try {
            return tryLock(lockName, this.zkTimeout, this.zkTimeUnit);
        }
        catch (Exception e) {
            throw ExceptionUtils.wrap(e);
        }
    }

    @Override
    public boolean tryLock(String lockName, long time, TimeUnit unit) {
        try {
            return getMutexLock(lockName).acquire(time, unit);
        }
        catch (Exception e) {
            throw ExceptionUtils.wrap(e);
        }
    }

}

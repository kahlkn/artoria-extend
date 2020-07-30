package artoria.time;

import artoria.thread.SimpleThreadFactory;
import artoria.util.Assert;
import artoria.util.ShutdownHookUtils;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;

import static artoria.common.Constants.ONE;
import static artoria.common.Constants.ZERO;
import static java.lang.Boolean.TRUE;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * Use a separate thread to get the timestamp of the current time and cache it.
 * @see System#currentTimeMillis()
 * @author Kahle
 */
public class CachedClock implements Clock {
    private static final String THREAD_NAME = "cached-clock-executor";

    public static long currentTimeMillis() {

        return Holder.INSTANCE.getTime();
    }

    public static Clock getInstance() {

        return Holder.INSTANCE;
    }

    private static class Holder {

        private static final CachedClock INSTANCE = new CachedClock(ONE);
    }

    private volatile long nowTime;

    private CachedClock(long period) {
        Assert.isTrue(period > ZERO, "Parameter \"period\" must greater than 0. ");
        ThreadFactory threadFactory = new SimpleThreadFactory(THREAD_NAME, TRUE);
        ScheduledThreadPoolExecutor threadPool = new ScheduledThreadPoolExecutor(ONE, threadFactory);
        threadPool.scheduleAtFixedRate(new TimeUpdater(), period, period, MILLISECONDS);
        ShutdownHookUtils.addExecutorService(threadPool);
        this.nowTime = System.currentTimeMillis();
    }

    @Override
    public long getTime() {

        return nowTime;
    }

    private class TimeUpdater implements Runnable {
        @Override
        public void run() {
            // Update time.
            nowTime = System.currentTimeMillis();
        }
    }

}

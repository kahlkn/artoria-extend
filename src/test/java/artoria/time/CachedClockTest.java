package artoria.time;

import artoria.logging.Logger;
import artoria.logging.LoggerFactory;
import org.junit.Test;

public class CachedClockTest {
    private static Logger log = LoggerFactory.getLogger(CachedClockTest.class);

    @Test
    public void test1() {
        Clock clock = CachedClock.getInstance();
        log.info("{}", DateUtils.format(clock.getTime()));
        log.info("{}", DateUtils.format(clock.getTime()));
        log.info("{}", DateUtils.format(clock.getTime()));
        log.info("{}", DateUtils.format(clock.getTime()));
        log.info("{}", DateUtils.format(clock.getTime()));
    }

}

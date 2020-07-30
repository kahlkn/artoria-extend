package artoria.property;

import artoria.logging.Logger;
import artoria.logging.LoggerFactory;
import org.junit.Test;

public class PropertyUtilsTest {
    private static Logger log = LoggerFactory.getLogger(PropertyUtilsTest.class);

    @Test
    public void test1() {
        PropertyUtils.setProperty("default_time", "1000");
        PropertyUtils.setProperty("default_switch", false);
        log.info("default_time: {}", PropertyUtils.getIntegerProperty("default_time"));
        log.info("default_time1: {}", PropertyUtils.getIntegerProperty("default_time1"));
        log.info("default_switch: {}", PropertyUtils.getBooleanProperty("default_switch"));
    }

}

package artoria.option;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleOptionProviderTest {
    private static Logger log = LoggerFactory.getLogger(SimpleOptionProviderTest.class);

    @Test
    public void test1() {
        OptionUtils.setOption("test_key", true);
        log.info("Test set: {}", OptionUtils.getOption("test_key"));
    }

}

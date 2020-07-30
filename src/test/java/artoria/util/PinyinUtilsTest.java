package artoria.util;

import artoria.logging.Logger;
import artoria.logging.LoggerFactory;
import org.junit.Test;

public class PinyinUtilsTest {
    private static Logger log = LoggerFactory.getLogger(PinyinUtilsTest.class);

    @Test
    public void test1() {
        log.info("{}", PinyinUtils.convertChineseToPinyin("世界，你好！", false));
        log.info("{}", PinyinUtils.convertChineseToPinyin("世界，你好！", true));
    }

}

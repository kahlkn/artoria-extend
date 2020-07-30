package artoria.data;

import artoria.data.mask.DataMaskAutoConfiguration;
import artoria.data.mask.DataMaskUtils;
import artoria.data.mask.WithPhoneNumberMasker;
import artoria.exception.ExceptionUtils;
import artoria.logging.Logger;
import artoria.logging.LoggerFactory;
import org.junit.Test;

public class DataMaskUtilsTest {
    private static Logger log = LoggerFactory.getLogger(DataMaskUtilsTest.class);

    static {
        try {
            new DataMaskAutoConfiguration().afterPropertiesSet();
        }
        catch (Exception e) {
            throw ExceptionUtils.wrap(e);
        }
    }

    @Test
    public void testPhoneNumber() {
        log.info("{}", DataMaskUtils.mask("PhoneNumber", "13600006666"));
        log.info("{}", DataMaskUtils.mask("PhoneNumber", "13888889999"));
    }

    @Test
    public void testWithPhoneNumber() {
        DataMaskUtils.register("WithPhoneNumber", new WithPhoneNumberMasker());
        log.info("{}", DataMaskUtils.mask("WithPhoneNumber", "Hello13600006666Hel13888889999"));
        log.info("{}", DataMaskUtils.mask("WithPhoneNumber", "1360000666613888889999"));
        log.info("{}", DataMaskUtils.mask("WithPhoneNumber", "Hello1354385689476556"));
    }

}

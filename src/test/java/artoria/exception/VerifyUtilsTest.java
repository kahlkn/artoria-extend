package artoria.exception;

import artoria.logging.Logger;
import artoria.logging.LoggerFactory;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static artoria.exception.SystemErrorCode.PASSWORD_IS_REQUIRED;
import static artoria.exception.SystemErrorCode.USERNAME_IS_REQUIRED;

public class VerifyUtilsTest {
    private Logger log = LoggerFactory.getLogger(VerifyUtilsTest.class);

    @Test
    public void test1() {
        try {
            Map<String, Object> data = new HashMap<String, Object>();
            VerifyUtils.notEmpty(data, USERNAME_IS_REQUIRED);
        }
        catch (BusinessException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Test
    public void test2() {
        try {
            Map<String, Object> data = new HashMap<String, Object>();
            VerifyUtils.notEmpty(data, PASSWORD_IS_REQUIRED);
        }
        catch (BusinessException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Test
    public void test3() {
        try {
            Map<String, Object> data = new HashMap<String, Object>();
            VerifyUtils.notEmpty(data, (ErrorCode) null);
        }
        catch (BusinessException e) {
            log.error(e.getMessage(), e);
        }
    }

}

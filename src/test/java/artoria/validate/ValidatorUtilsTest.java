package artoria.validate;

import artoria.logging.Logger;
import artoria.logging.LoggerFactory;
import org.junit.Test;

public class ValidatorUtilsTest {
    private static Logger log = LoggerFactory.getLogger(ValidatorUtilsTest.class);

    @Test
    public void testNumeric() {
        ValidatorUtils.register("numeric", new NumericValidator());
        log.info("{}", ValidatorUtils.validate("numeric", "888.666"));
        log.info("{}", ValidatorUtils.validate("numeric", "-888.666"));
        log.info("{}", ValidatorUtils.validate("numeric", "+888.666"));
        log.info("{}", ValidatorUtils.validate("numeric", "888.666w"));
        log.info("{}", ValidatorUtils.validate("numeric", "hello, world! "));
    }

    @Test
    public void testEmail() {
        String emailRegex = "^[0-9A-Za-z][\\.-_0-9A-Za-z]*@[0-9A-Za-z]+(?:\\.[0-9A-Za-z]+)+$";
        ValidatorUtils.register("regex_email", new RegexValidator(emailRegex));
        log.info("{}", ValidatorUtils.validate("regex_email", "hello@email.com"));
        log.info("{}", ValidatorUtils.validate("regex_email", "hello@vip.email.com"));
        log.info("{}", ValidatorUtils.validate("regex_email", "$hello@email.com"));
        log.info("{}", ValidatorUtils.validate("regex_email", "hello@email"));
        log.info("{}", ValidatorUtils.validate("regex_email", "hello@.com"));
    }

    @Test
    public void testPhoneNumber() {
        String phoneNumberRegex = "^1[3|4|5|6|7|8|9]\\d{9}$";
        ValidatorUtils.register("regex_phone_number", new RegexValidator(phoneNumberRegex));
        log.info("{}", ValidatorUtils.validate("regex_phone_number", "13000000000"));
        log.info("{}", ValidatorUtils.validate("regex_phone_number", "18000000000"));
        log.info("{}", ValidatorUtils.validate("regex_phone_number", "19999999999"));
    }

}

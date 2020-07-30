package artoria.util;

import org.junit.Test;

public class PhoneNumberUtilsTest {

    @Test
    public void test1() {
        String phoneCarrierZh = PhoneNumberUtils.phoneCarrierZh("18658258192");
        System.out.println(phoneCarrierZh);
    }

}

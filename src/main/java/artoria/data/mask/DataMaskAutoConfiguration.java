package artoria.data.mask;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;

/**
 * Data mask auto configuration.
 * @author Kahle
 */
@Configuration
public class DataMaskAutoConfiguration implements InitializingBean, DisposableBean {

    @Override
    public void afterPropertiesSet() throws Exception {
        DataMaskUtils.register("PhoneNumber", new PhoneNumberMasker());
        DataMaskUtils.register("WithPhoneNumber", new WithPhoneNumberMasker());
        DataMaskUtils.register("BankCardNumber", new BankCardNumberMasker());
    }

    @Override
    public void destroy() throws Exception {
    }

}

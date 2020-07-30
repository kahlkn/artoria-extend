package artoria.validate;

import artoria.util.MapUtils;
import artoria.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * Validate auto configuration.
 * @author Kahle
 */
@Configuration
@EnableConfigurationProperties({ValidateProperties.class})
public class ValidateAutoConfiguration implements InitializingBean, DisposableBean {
    private static Logger log = LoggerFactory.getLogger(ValidateAutoConfiguration.class);
    private final ValidateProperties validateProperties;

    @Autowired
    public ValidateAutoConfiguration(ValidateProperties validateProperties) {

        this.validateProperties = validateProperties;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.registerDefaultValidator();
        this.registerConfiguredRegexValidator();
    }

    @Override
    public void destroy() throws Exception {
    }

    private void registerDefaultValidator() {
        String urlRegex = "^(?:([A-Za-z]+):)?(\\/{0,3})([0-9.\\-A-Za-z]+)(?::(\\d+))?(?:\\/([^?#]*))?(?:\\?([^#]*))?(?:#(.*))?$";
        String qqRegex = "^[1-9][0-9]{4,14}$";
        String emailRegex = "^[0-9A-Za-z][\\.-_0-9A-Za-z]*@[0-9A-Za-z]+(?:\\.[0-9A-Za-z]+)+$";
        String weChatRegex = "^[a-zA-Z]{1}[-_a-zA-Z0-9]{5,19}+$";
        String numericRegex = "^(-|\\+)?\\d+\\.?\\d*$";
        String bankCardNumberRegex = "^([1-9]{1})(\\d{14}|\\d{18})$";
        String phoneNumberRegex = "^1[3|4|5|6|7|8|9]\\d{9}$";
        ValidatorUtils.register("regex_url", new RegexValidator(urlRegex));
        ValidatorUtils.register("regex_qq", new RegexValidator(qqRegex));
        ValidatorUtils.register("regex_email", new RegexValidator(emailRegex));
        ValidatorUtils.register("regex_wechat", new RegexValidator(weChatRegex));
        ValidatorUtils.register("regex_numeric", new RegexValidator(numericRegex));
        ValidatorUtils.register("regex_phone_number", new RegexValidator(phoneNumberRegex));
        ValidatorUtils.register("regex_bank_card_number", new RegexValidator(bankCardNumberRegex));
        ValidatorUtils.register("luhn_bank_card_number", new BankCardNumberLuhnValidator());
    }

    private void registerConfiguredRegexValidator() {
        if (validateProperties == null) { return; }
        Map<String, String> regexValidators =
                validateProperties.getRegexValidators();
        if (MapUtils.isEmpty(regexValidators)) { return; }
        for (Map.Entry<String, String> entry : regexValidators.entrySet()) {
            String regex = entry.getValue();
            if (StringUtils.isBlank(regex)) { continue; }
            String name = entry.getKey();
            if (StringUtils.isBlank(name)) { continue; }
            ValidatorUtils.register(name, new RegexValidator(regex));
        }
    }

}

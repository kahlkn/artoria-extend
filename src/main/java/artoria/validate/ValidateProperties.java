package artoria.validate;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Validate properties.
 * @author Kahle
 */
@ConfigurationProperties("artoria.validate")
public class ValidateProperties {
    /**
     * Regex validator configuration, and key is name and value is regex.
     */
    private Map<String, String> regexValidators = new HashMap<String, String>();

    public Map<String, String> getRegexValidators() {

        return regexValidators;
    }

    public void setRegexValidators(Map<String, String> regexValidators) {

        this.regexValidators = regexValidators;
    }

}

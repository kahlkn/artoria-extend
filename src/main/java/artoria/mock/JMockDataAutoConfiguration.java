package artoria.mock;

import com.github.jsonzou.jmockdata.JMockData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass({JMockData.class})
public class JMockDataAutoConfiguration {
    private static Logger log = LoggerFactory.getLogger(JMockDataAutoConfiguration.class);

    public JMockDataAutoConfiguration() {
        MockUtils.setMockProvider(new JMockDataProvider());
        log.info("The \"JMockData\" was initialized success. ");
    }

}

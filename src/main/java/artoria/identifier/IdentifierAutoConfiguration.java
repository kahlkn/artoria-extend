package artoria.identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static artoria.common.Constants.MINUS;

@Configuration
public class IdentifierAutoConfiguration implements InitializingBean, DisposableBean {
    private static Logger log = LoggerFactory.getLogger(IdentifierAutoConfiguration.class);
    private static StringIdentifierGenerator uuidRawGenerator = new SimpleIdGenerator(MINUS);
    private static StringIdentifierGenerator uuidGenerator = new SimpleIdGenerator();
    private static LongIdentifierGenerator snowFlakeIdGenerator = new SnowFlakeIdGenerator();

    @Override
    public void afterPropertiesSet() throws Exception {
        IdentifierUtils.setStringIdentifierGenerator(uuidGenerator);
        log.info("The identifier tools was initialized success. ");
    }

    @Override
    public void destroy() throws Exception {
    }

    @Bean(name = "uuidRawGenerator")
    public StringIdentifierGenerator uuidRawGenerator() {

        return uuidRawGenerator;
    }

    @Bean(name = "uuidGenerator")
    public StringIdentifierGenerator uuidGenerator() {

        return uuidGenerator;
    }

    @Bean(name = "snowFlakeIdGenerator")
    public LongIdentifierGenerator snowFlakeIdGenerator() {

        return snowFlakeIdGenerator;
    }

}

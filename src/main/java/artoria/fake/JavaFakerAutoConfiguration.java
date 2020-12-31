package artoria.fake;

import com.github.javafaker.Faker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass({Faker.class})
public class JavaFakerAutoConfiguration {
    private static final String[] FAKER_NAMES = new String[] {"ancient", "app", "artist", "avatar", "lorem", "music", "name", "number", "internet", "phone_number", "pokemon", "address", "business", "book", "color", "currency", "company", "id_number", "date_and_time", "dog", "bool", "team", "university", "cat", "file", "job", "weather", "medical"};
    private static Logger log = LoggerFactory.getLogger(JavaFakerAutoConfiguration.class);
    private Faker faker;

    public JavaFakerAutoConfiguration() {
        log.info("Start loading \"javafaker\". ");
        faker = new Faker();
        for (String fakerName : FAKER_NAMES) {
            FakeUtils.register(new JavaFakerFaker(faker, fakerName));
        }
        log.info("The \"javafaker\" was initialized success. ");
    }

}

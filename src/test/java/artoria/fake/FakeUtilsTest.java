package artoria.fake;

import artoria.logging.Logger;
import artoria.logging.LoggerFactory;
import com.github.javafaker.Faker;
import org.junit.Test;

public class FakeUtilsTest {
    private static Logger log = LoggerFactory.getLogger(FakeUtilsTest.class);

    @Test
    public void testFake1() {
        FakeUtils.register(new ChineseNameFaker());
        log.info("name: {}", FakeUtils.fake(String.class, "name"));
        log.info("name.firstName: {}", FakeUtils.fake(String.class, "name.firstName"));
        log.info("name.middleName: {}", FakeUtils.fake(String.class, "name.middleName"));
        log.info("name.lastName: {}", FakeUtils.fake(String.class, "name.lastName"));
        log.info("name.full_name: {}", FakeUtils.fake(String.class, "name.full_name"));
        log.info("name.full_name[lang:en]: {}", FakeUtils.fake(String.class, "name.full_name[lang:en]"));
    }

    @Test
    public void testFake2() {
//        new JavaFakerAutoConfiguration();
        Faker faker = new Faker();
        FakeUtils.register(new JavaFakerFaker(faker, "job"));
        FakeUtils.register(new JavaFakerFaker(faker, "lorem"));
        FakeUtils.register(new JavaFakerFaker(faker, "phone_number"));
        FakeUtils.register(new JavaFakerFaker(faker, "address"));
        FakeUtils.register(new JavaFakerFaker(faker, "book"));
        FakeUtils.register(new JavaFakerFaker(faker, "company"));
        log.info("job.title: {}", FakeUtils.fake(String.class, "job.title"));
        log.info("job: {}", FakeUtils.fake(String.class, "job"));
        log.info("phone_number.cell_phone: {}", FakeUtils.fake(String.class, "phone_number.cell_phone"));
        log.info("phone_number.cell_phone: {}", FakeUtils.fake(String.class, "phone_number.cell_phone"));
        log.info("phone_number.cell_phone: {}", FakeUtils.fake(String.class, "phone_number.cell_phone"));
        log.info("phone_number: {}", FakeUtils.fake(String.class, "phone_number"));
        log.info("phone_number: {}", FakeUtils.fake(String.class, "phone_number"));
        log.info("phone_number.phone_number: {}", FakeUtils.fake(String.class, "phone_number.phone_number"));
        log.info("address: {}", FakeUtils.fake(String.class, "address"));
        log.info("address: {}", FakeUtils.fake(String.class, "address"));
        log.info("address.full_address: {}", FakeUtils.fake(String.class, "address.full_address"));
        log.info("company.name: {}", FakeUtils.fake(String.class, "company.name"));
        log.info("book.title: {}", FakeUtils.fake(String.class, "book.title"));
        log.info("lorem.paragraph: {}", FakeUtils.fake(String.class, "lorem.paragraph"));
    }

}

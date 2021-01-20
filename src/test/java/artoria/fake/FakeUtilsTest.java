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
        log.info("name: {}", FakeUtils.fake("name", String.class));
        log.info("name.firstName: {}", FakeUtils.fake("name.firstName", String.class));
        log.info("name.middleName: {}", FakeUtils.fake("name.middleName", String.class));
        log.info("name.lastName: {}", FakeUtils.fake("name.lastName", String.class));
        log.info("name.full_name: {}", FakeUtils.fake("name.full_name", String.class));
        log.info("name.full_name[lang:en]: {}", FakeUtils.fake("name.full_name[lang:en]", String.class));
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
        log.info("job.title: {}", FakeUtils.fake("job.title", String.class));
        log.info("job: {}", FakeUtils.fake("job", String.class));
        log.info("phone_number.cell_phone: {}", FakeUtils.fake("phone_number.cell_phone", String.class));
        log.info("phone_number.cell_phone: {}", FakeUtils.fake("phone_number.cell_phone", String.class));
        log.info("phone_number.cell_phone: {}", FakeUtils.fake("phone_number.cell_phone", String.class));
        log.info("phone_number: {}", FakeUtils.fake("phone_number", String.class));
        log.info("phone_number: {}", FakeUtils.fake("phone_number", String.class));
        log.info("phone_number.phone_number: {}", FakeUtils.fake("phone_number.phone_number", String.class));
        log.info("address: {}", FakeUtils.fake("address", String.class));
        log.info("address: {}", FakeUtils.fake("address", String.class));
        log.info("address.full_address: {}", FakeUtils.fake("address.full_address", String.class));
        log.info("company.name: {}", FakeUtils.fake("company.name", String.class));
        log.info("book.title: {}", FakeUtils.fake("book.title", String.class));
        log.info("lorem.paragraph: {}", FakeUtils.fake("lorem.paragraph", String.class));
    }

}

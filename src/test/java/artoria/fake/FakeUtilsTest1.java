package artoria.fake;

import artoria.logging.Logger;
import artoria.logging.LoggerFactory;
import artoria.test.bean.Book;
import artoria.test.bean.User;
import com.alibaba.fastjson.JSON;
import org.junit.Test;

import java.util.List;

import static java.lang.Boolean.TRUE;

public class FakeUtilsTest1 {
    private static Logger log = LoggerFactory.getLogger(FakeUtilsTest1.class);

    static {
        new JavaFakerAutoConfiguration();
    }

    @Test
    public void testFake1() {
        User user = FakeUtils.fake(User.class,
                "name=name|gender=dog.gender|nickname=name.firstName|phoneNumber=phone_number|introduce=lorem.paragraph");
        log.info("Fake user: {}", JSON.toJSONString(user, TRUE));
        Book book = FakeUtils.fake(Book.class, "name=book.title|author=book.author|publisher=book.publisher");
        log.info("Fake book: {}", JSON.toJSONString(book, TRUE));
    }

    @Test
    public void testFakeList1() {
        List<User> userList = FakeUtils.fakeList(User.class,
                "name=name|gender=dog.gender|nickname=name.firstName|phoneNumber=phone_number|introduce=lorem.paragraph");
        log.info("Fake user list: {}", JSON.toJSONString(userList, TRUE));
    }

    @Test
    public void testFakeList2() {
        List<Book> bookList = FakeUtils.fakeList(Book.class, "name=book.title|author=book.author|publisher=book.publisher");
        log.info("Fake book list: {}", JSON.toJSONString(bookList, TRUE));
    }

}

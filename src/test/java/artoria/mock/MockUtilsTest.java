package artoria.mock;

import artoria.logging.Logger;
import artoria.logging.LoggerFactory;
import artoria.test.bean.Book;
import artoria.test.bean.Menu;
import artoria.test.bean.NestedBean;
import artoria.util.TypeUtils;
import com.alibaba.fastjson.JSON;
import org.junit.Test;

import java.util.List;

public class MockUtilsTest {
    private static Logger log = LoggerFactory.getLogger(MockUtilsTest.class);

    static {

        MockUtils.setMockProvider(new JMockDataProvider());
    }

    @Test
    public void testMock1() {
        Book book = MockUtils.mock(Book.class);
        log.info(JSON.toJSONString(book));
    }

    @Test
    public void testMock2() {
        List<Book> bookList = MockUtils.mock(TypeUtils.parameterizedOf(List.class, Book.class));
        log.info(JSON.toJSONString(bookList, Boolean.TRUE));
    }

    @Test
    public void testMock3() {
        Menu menu = MockUtils.mock(Menu.class);
        System.out.println(JSON.toJSONString(menu));
    }

}

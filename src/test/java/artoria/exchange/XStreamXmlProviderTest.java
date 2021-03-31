package artoria.exchange;

import artoria.entity.Student;
import artoria.logging.Logger;
import artoria.logging.LoggerFactory;
import artoria.mock.MockUtils;
import com.alibaba.fastjson.JSON;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class XStreamXmlProviderTest {
    private static Logger log = LoggerFactory.getLogger(XStreamXmlProviderTest.class);
    private XmlProvider xmlProvider = new XStreamXmlProvider();

    @Test
    public void test1() {
        List<Student> list = new ArrayList<Student>();
        list.add(MockUtils.mock(Student.class));
        list.add(MockUtils.mock(Student.class));

        XmlFeature[] features = new XmlFeature[] {
                new XmlFieldAlias("id", Student.class, "studentId"),
                new XmlFieldAlias("schName", Student.class, "schoolName"),
                new XmlClassAlias("student", Student.class),
                new XmlClassAlias("xml", List.class),
        };

        String xmlString = xmlProvider.toXmlString(list, features);
        log.info("\n{}", xmlString);
        List<Student> parseList = xmlProvider.parseObject(xmlString, List.class, features);
        log.info("\n{}", JSON.toJSONString(parseList, true));
    }

}

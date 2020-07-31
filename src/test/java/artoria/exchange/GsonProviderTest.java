package artoria.exchange;

import artoria.entity.Student;
import artoria.random.RandomUtils;
import artoria.util.TypeUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static artoria.exchange.JsonFormat.PRETTY_FORMAT;

public class GsonProviderTest {
    private Student data = new Student();
    private List<Student> data1 = new ArrayList<Student>();
    private String jsonString = null;
    private String jsonString1 = null;

    @Before
    public void init() {
        JsonUtils.setJsonProvider(new GsonProvider());
        data = RandomUtils.nextObject(Student.class);
        for (int i = 0; i < 5; i++) {
            data1.add(RandomUtils.nextObject(Student.class));
        }
        jsonString = JsonUtils.toJsonString(data, PRETTY_FORMAT);
        jsonString1 = JsonUtils.toJsonString(data1, PRETTY_FORMAT);
    }

    @Test
    public void test1() {
        System.out.println(JsonUtils.toJsonString(data, PRETTY_FORMAT));
        System.out.println(JsonUtils.toJsonString(data1, PRETTY_FORMAT));
        System.out.println(JsonUtils.toJsonString(data, PRETTY_FORMAT));
    }

    @Test
    public void test2() {
        JsonUtils.setJsonProvider(new GsonProvider());
        Student student = JsonUtils.parseObject(jsonString, Student.class);
        List<Student> list = JsonUtils.parseObject(jsonString1
                , TypeUtils.parameterizedOf(List.class, Student.class));
        System.out.println(JsonUtils.toJsonString(student, PRETTY_FORMAT));
        System.out.println("----");
        for (Student student1 : list) {
            System.out.println(JsonUtils.toJsonString(student1));
        }
    }

}

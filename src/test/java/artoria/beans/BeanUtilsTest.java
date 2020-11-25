package artoria.beans;

import artoria.entity.Person;
import artoria.entity.Student;
import artoria.fake.FakeUtils;
import com.alibaba.fastjson.JSON;
import org.junit.Test;

public class BeanUtilsTest {

    @Test
    public void testIgnoreCglibCopy() {
        Person person = FakeUtils.fake(Person.class);
        // BeanUtils.setBeanCopier(new CglibBeanCopier());
        BeanUtils.setBeanCopier(new SpringCglibBeanCopier());
        Student student = new Student();
        BeanUtils.copy(person, student);
        System.out.println(JSON.toJSONString(student));
    }

}

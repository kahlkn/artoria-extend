package artoria.beans;

import artoria.logging.Logger;
import artoria.logging.LoggerFactory;
import org.junit.Test;

public class SpringBeanCopierTest extends AbstractBeanCopierTest {
    private static Logger log = LoggerFactory.getLogger(SpringBeanCopierTest.class);
    private static BeanCopier beanCopier = new SpringBeanCopier();

    @Test
    public void testCopyObjectToMap() {

        doCopyObjectToMap(beanCopier);
    }

    @Test
    public void testCopyMapToObject() {

        doCopyMapToObject(beanCopier);
    }

    @Test
    public void testCopyNullToValue() {

        doCopyNullToValue(beanCopier);
    }

    @Test
    public void testCopyObjToObjToTestTypeConvert() {

        doCopyObjToObjToTestTypeConvert(beanCopier);
    }

    @Test
    public void testCopyObjToOtherObjToTestPropertyList() {

        doCopyObjToOtherObjToTestPropertyList(beanCopier);
    }

}

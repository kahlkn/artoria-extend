package artoria.beans;

import artoria.logging.Logger;
import artoria.logging.LoggerFactory;
import org.junit.Test;

public class ApacheBeanCopierTest extends AbstractBeanCopierTest {
    private static Logger log = LoggerFactory.getLogger(ApacheBeanCopierTest.class);
    private static BeanCopier beanCopier = new ApacheBeanCopier();

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

package artoria.beans;

import artoria.logging.Logger;
import artoria.logging.LoggerFactory;
import org.junit.Test;

public class SimpleBeanCopierTest extends AbstractBeanCopierTest {
    private static Logger log = LoggerFactory.getLogger(SimpleBeanCopierTest.class);
    private static BeanCopier beanCopier = new SimpleBeanCopier();

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
        ((SimpleBeanCopier) beanCopier).setIgnoreException(false);
        doCopyObjToObjToTestTypeConvert(beanCopier);
    }

    @Test
    public void testCopyObjToOtherObjToTestPropertyList() {

        doCopyObjToOtherObjToTestPropertyList(beanCopier);
    }

}

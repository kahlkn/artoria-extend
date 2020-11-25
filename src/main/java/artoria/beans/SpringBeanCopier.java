package artoria.beans;

import artoria.convert.type.TypeConverter;
import org.springframework.beans.BeanUtils;

/**
 * Spring bean copier.
 * @author Kahle
 */
public class SpringBeanCopier implements BeanCopier {

    @Override
    public void copy(Object from, Object to, TypeConverter typeConverter) {

        BeanUtils.copyProperties(from, to);
    }

}

package artoria.beans;

import artoria.convert.TypeConverter;
import artoria.exception.ExceptionUtils;
import org.apache.commons.beanutils.BeanUtils;

/**
 * Apache bean copier.
 * @author Kahle
 */
public class ApacheBeanCopier implements BeanCopier {

    @Override
    public void copy(Object from, Object to, TypeConverter typeConverter) {
        try {
            BeanUtils.copyProperties(to, from);
        }
        catch (Exception e) {
            throw ExceptionUtils.wrap(e);
        }
    }

}

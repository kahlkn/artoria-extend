package artoria.beans;

import artoria.convert.ConversionProvider;
import org.springframework.beans.BeanUtils;

/**
 * Spring bean copier.
 * @author Kahle
 */
public class SpringBeanCopier implements BeanCopier {

    @Override
    public void copy(Object from, Object to, ConversionProvider conversionProvider) {

        BeanUtils.copyProperties(from, to);
    }

}

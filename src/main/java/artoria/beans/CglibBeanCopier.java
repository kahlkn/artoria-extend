package artoria.beans;

import artoria.convert.type1.ConversionProvider;
import artoria.util.Assert;

/**
 * Cglib bean copier.
 * @author Kahle
 */
public class CglibBeanCopier implements BeanCopier {

    private static class CglibConverterAdapter implements net.sf.cglib.core.Converter {
        private final ConversionProvider conversionProvider;
        private final Boolean useConversion;

        public CglibConverterAdapter(ConversionProvider conversionProvider) {
            this.conversionProvider = conversionProvider;
            this.useConversion = conversionProvider != null;
        }

        @Override
        public Object convert(Object value, Class target, Object context) {

            return useConversion ? conversionProvider.convert(value, target) : value;
        }

    }

    @Override
    public void copy(Object from, Object to, ConversionProvider conversionProvider) {
        Assert.notNull(from, "Parameter \"from\" must is not null. ");
        Assert.notNull(to, "Parameter \"to\" must is not null. ");
        Class<?> fromClass = from.getClass();
        Class<?> toClass = to.getClass();
        boolean useConverter = conversionProvider != null;
        net.sf.cglib.beans.BeanCopier copier =
                net.sf.cglib.beans.BeanCopier.create(fromClass, toClass, useConverter);
        CglibConverterAdapter adapter = new CglibConverterAdapter(conversionProvider);
        copier.copy(from, to, adapter);
    }

}

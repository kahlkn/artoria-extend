package artoria.beans;

import artoria.convert.type1.ConversionProvider;
import artoria.util.Assert;

/**
 * Spring cglib bean copier.
 * @author Kahle
 */
public class SpringCglibBeanCopier implements BeanCopier {

    private static class SpringCglibConverterAdapter implements org.springframework.cglib.core.Converter {
        private final ConversionProvider conversionProvider;
        private final Boolean useConversion;

        public SpringCglibConverterAdapter(ConversionProvider conversionProvider) {
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
        org.springframework.cglib.beans.BeanCopier copier =
                org.springframework.cglib.beans.BeanCopier.create(fromClass, toClass, useConverter);
        SpringCglibConverterAdapter adapter = new SpringCglibConverterAdapter(conversionProvider);
        copier.copy(from, to, adapter);
    }

}

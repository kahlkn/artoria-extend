package artoria.beans;

import artoria.convert.type.TypeConverter;
import artoria.util.Assert;

/**
 * Spring cglib bean copier.
 * @author Kahle
 */
public class SpringCglibBeanCopier implements BeanCopier {

    private static class SpringCglibConverterAdapter implements org.springframework.cglib.core.Converter {
        private final TypeConverter typeConverter;
        private final boolean useConverter;

        public SpringCglibConverterAdapter(TypeConverter typeConverter) {
            this.typeConverter = typeConverter;
            this.useConverter = typeConverter != null;
        }

        @Override
        public Object convert(Object value, Class valClass, Object methodName) {

            return useConverter ? typeConverter.convert(value, valClass) : value;
        }

    }

    @Override
    public void copy(Object from, Object to, TypeConverter typeConverter) {
        Assert.notNull(from, "Parameter \"from\" must is not null. ");
        Assert.notNull(to, "Parameter \"to\" must is not null. ");
        Class<?> fromClass = from.getClass();
        Class<?> toClass = to.getClass();
        boolean useConverter = typeConverter != null;
        org.springframework.cglib.beans.BeanCopier copier =
                org.springframework.cglib.beans.BeanCopier.create(fromClass, toClass, useConverter);
        SpringCglibConverterAdapter adapter = new SpringCglibConverterAdapter(typeConverter);
        copier.copy(from, to, adapter);
    }

}

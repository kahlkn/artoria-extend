package artoria.beans;

import artoria.converter.TypeConverter;
import artoria.util.Assert;

/**
 * Spring cglib bean copier.
 * @author Kahle
 */
public class SpringCglibBeanCopier implements BeanCopier {

    private static class SpringCglibConverterAdapter implements org.springframework.cglib.core.Converter {
        private TypeConverter typeConverter;
        private boolean useConverter;

        public SpringCglibConverterAdapter(TypeConverter typeConverter) {
            this.setTypeConverter(typeConverter);
            this.useConverter = typeConverter != null;
        }

        public void setTypeConverter(TypeConverter typeConverter) {
            Assert.notNull(typeConverter, "Parameter \"typeConverter\" must not null. ");
            this.typeConverter = typeConverter;
        }

        @Override
        public Object convert(Object value, Class valClass, Object methodName) {

            return useConverter ? typeConverter.convert(value, valClass) : value;
        }

    }

    @Override
    public void copy(Object from, Object to, TypeConverter typeConverter) {
        Class<?> fromClass = from.getClass();
        Class<?> toClass = to.getClass();
        boolean useConverter = typeConverter != null;
        org.springframework.cglib.beans.BeanCopier copier =
                org.springframework.cglib.beans.BeanCopier.create(fromClass, toClass, useConverter);
        SpringCglibConverterAdapter adapter = new SpringCglibConverterAdapter(typeConverter);
        copier.copy(from, to, adapter);
    }

}

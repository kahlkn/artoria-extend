package artoria.beans;

import artoria.converter.TypeConverter;
import artoria.util.Assert;

/**
 * Cglib bean copier.
 * @author Kahle
 */
public class CglibBeanCopier implements BeanCopier {

    private static class CglibConverterAdapter implements net.sf.cglib.core.Converter {
        private TypeConverter typeConverter;
        private boolean useConverter;

        public CglibConverterAdapter(TypeConverter typeConverter) {
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
        net.sf.cglib.beans.BeanCopier copier =
                net.sf.cglib.beans.BeanCopier.create(fromClass, toClass, useConverter);
        CglibConverterAdapter adapter = new CglibConverterAdapter(typeConverter);
        copier.copy(from, to, adapter);
    }

}

package artoria.beans;

import artoria.convert.type.TypeConverter;
import artoria.util.Assert;

/**
 * Cglib bean copier.
 * @author Kahle
 */
public class CglibBeanCopier implements BeanCopier {

    private static class CglibConverterAdapter implements net.sf.cglib.core.Converter {
        private final TypeConverter typeConverter;
        private final Boolean useConverter;

        public CglibConverterAdapter(TypeConverter typeConverter) {
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
        net.sf.cglib.beans.BeanCopier copier =
                net.sf.cglib.beans.BeanCopier.create(fromClass, toClass, useConverter);
        CglibConverterAdapter adapter = new CglibConverterAdapter(typeConverter);
        copier.copy(from, to, adapter);
    }

}

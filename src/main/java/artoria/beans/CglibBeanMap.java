package artoria.beans;

import artoria.converter.TypeConverter;

import java.util.Set;

/**
 * Cglib bean map.
 * @author Kahle
 */
public class CglibBeanMap extends BeanMap {

    private net.sf.cglib.beans.BeanMap beanMap;

    @Override
    public void setBean(Object bean) {
        super.setBean(bean);
        this.beanMap = net.sf.cglib.beans.BeanMap.create(bean);
    }

    @Override
    protected Object get(Object bean, Object key) {

        return this.beanMap.get(key);
    }

    @Override
    protected Object put(Object bean, Object key, Object value) {
        TypeConverter cvt = this.getTypeConverter();
        if (key != null && cvt != null) {
            Class type = this.beanMap.getPropertyType((String) key);
            value = cvt.convert(value, type);
        }
        return this.beanMap.put(key, value);
    }

    @Override
    public Set keySet() {

        return this.beanMap.keySet();
    }

}

package artoria.beans;

import artoria.converter.TypeConverter;

import java.util.Set;

/**
 * Spring cglib bean map.
 * @author Kahle
 */
public class SpringCglibBeanMap extends BeanMap {
    private org.springframework.cglib.beans.BeanMap beanMap;

    @Override
    public void setBean(Object bean) {
        super.setBean(bean);
        this.beanMap = org.springframework.cglib.beans.BeanMap.create(bean);
    }

    @Override
    protected Object get(Object bean, Object key) {

        return beanMap.get(key);
    }

    @Override
    protected Object put(Object bean, Object key, Object value) {
        TypeConverter cvt = getTypeConverter();
        if (key != null && cvt != null) {
            Class type = beanMap.getPropertyType((String) key);
            value = cvt.convert(value, type);
        }
        return beanMap.put(key, value);
    }

    @Override
    public Set keySet() {

        return beanMap.keySet();
    }

}

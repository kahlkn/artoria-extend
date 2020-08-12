package artoria.property;

import artoria.convert.TypeConvertUtils;
import artoria.util.Assert;
import artoria.util.ObjectUtils;

import static artoria.common.Constants.DEFAULT;

public abstract class AbstractPropertyProvider implements PropertyProvider {
    protected static final String DISPLAY_RELOAD_LOG = "display_property_provider_reload_log";

    @Override
    public <T> T getRequiredProperty(String name, Class<T> targetType) {
        Assert.notNull(targetType, "Parameter \"targetType\" must not null. ");
        T value = getProperty(name, targetType, null);
        Assert.state(value != null, "The property value is null. ");
        return value;
    }

    @Override
    public <T> T getProperty(String name, Class<T> targetType, T defaultValue) {
        Assert.notNull(targetType, "Parameter \"targetType\" must not null. ");
        Object value = getProperty(name, defaultValue);
        if (value == null) { return null; }
        value = TypeConvertUtils.convert(value, targetType);
        return ObjectUtils.cast(value, targetType);
    }

    @Override
    public Object setProperty(String name, Object value) {

        return setProperty(DEFAULT, name, value);
    }

}

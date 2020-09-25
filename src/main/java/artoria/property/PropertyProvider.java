package artoria.property;

import java.util.Map;

public interface PropertyProvider {

    boolean containsProperty(String name);

    <T> T getRequiredProperty(String name, Class<T> targetType);

    <T> T getProperty(String name, Class<T> targetType, T defaultValue);

    Object getProperty(String name, Object defaultValue);

    Object setProperty(String name, Object value);

    Object setProperty(String group, String name, Object value);

    Object removeProperty(String name);

    Map<String, Object> getProperties(String group);

    void reload(Object data);

}

package artoria.property;

import java.util.Map;

public interface PropertyProvider {

    <T> T getRequiredProperty(String name, Class<T> targetType);

    <T> T getProperty(String name, Class<T> targetType, T defaultValue);

    boolean containsProperty(String name);

    Map<String, Object> getProperties(String group);

    Object getProperty(String name, Object defaultValue);

    Object removeProperty(String name);

    Object setProperty(String name, Object value);

    Object setProperty(String group, String name, Object value);

    void reload(Map<String, Map<String, Object>> data);

}

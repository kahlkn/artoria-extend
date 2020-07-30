package artoria.property;

import artoria.logging.Logger;
import artoria.logging.LoggerFactory;
import artoria.util.Assert;

import java.util.Map;

import static artoria.common.Constants.ZERO;

public class PropertyUtils {
    private static final PropertyProvider DEFAULT_PROPERTY_PROVIDER = new SimplePropertyProvider();
    private static Logger log = LoggerFactory.getLogger(PropertyUtils.class);
    private static PropertyProvider propertyProvider;

    public static PropertyProvider getPropertyProvider() {

        return propertyProvider != null ? propertyProvider : DEFAULT_PROPERTY_PROVIDER;
    }

    public static void setPropertyProvider(PropertyProvider propertyProvider) {
        Assert.notNull(propertyProvider, "Parameter \"propertyProvider\" must not null. ");
        log.info("Set property provider: {}", propertyProvider.getClass().getName());
        PropertyUtils.propertyProvider = propertyProvider;
    }

    public static String getStringProperty(String name) {

        return getStringProperty(name, null);
    }

    public static String getStringProperty(String name, String defaultValue) {

        return getProperty(name, String.class, defaultValue);
    }

    public static boolean getBooleanProperty(String name) {

        return getBooleanProperty(name, Boolean.FALSE);
    }

    public static boolean getBooleanProperty(String name, boolean defaultValue) {

        return getProperty(name, Boolean.class, defaultValue);
    }

    public static int getIntegerProperty(String name) {

        return getIntegerProperty(name, ZERO);
    }

    public static int getIntegerProperty(String name, int defaultValue) {

        return getProperty(name, Integer.class, defaultValue);
    }

    public static <T> T getRequiredProperty(String name, Class<T> targetType) {

        return getPropertyProvider().getRequiredProperty(name, targetType);
    }

    public static <T> T getProperty(String name, Class<T> targetType, T defaultValue) {

        return getPropertyProvider().getProperty(name, targetType, defaultValue);
    }

    public static boolean containsProperty(String name) {

        return getPropertyProvider().containsProperty(name);
    }

    public static Map<String, Object> getProperties(String group) {

        return getPropertyProvider().getProperties(group);
    }

    public static Object getProperty(String name, Object defaultValue) {

        return getPropertyProvider().getProperty(name, defaultValue);
    }

    public static Object removeProperty(String name) {

        return getPropertyProvider().removeProperty(name);
    }

    public static Object setProperty(String name, Object value) {

        return getPropertyProvider().setProperty(name, value);
    }

    public static Object setProperty(String group, String name, Object value) {

        return getPropertyProvider().setProperty(group, name, value);
    }

}

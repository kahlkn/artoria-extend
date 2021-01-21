package artoria.option;

import artoria.util.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static artoria.common.Constants.EMPTY_STRING;
import static artoria.common.Constants.NULL_OBJ;

public class OptionUtils {
    private static Logger log = LoggerFactory.getLogger(OptionUtils.class);
    private static OptionProvider optionProvider;

    public static OptionProvider getOptionProvider() {
        if (optionProvider != null) { return optionProvider; }
        synchronized (OptionUtils.class) {
            if (optionProvider != null) { return optionProvider; }
            OptionUtils.setOptionProvider(new SimpleOptionProvider());
            return optionProvider;
        }
    }

    public static void setOptionProvider(OptionProvider optionProvider) {
        Assert.notNull(optionProvider, "Parameter \"optionProvider\" must not null. ");
        log.info("Set option provider: {}", optionProvider.getClass().getName());
        OptionUtils.optionProvider = optionProvider;
    }

    public static boolean containsOption(String name) {

        return containsOption(EMPTY_STRING, name);
    }

    public static boolean containsOption(String owner, String name) {

        return getOptionProvider().containsOption(owner, name);
    }

    public static Map<String, Object> getOptions(String owner) {

        return getOptionProvider().getOptions(owner);
    }

    public static Object getOption(String name) {

        return getOption(EMPTY_STRING, name, NULL_OBJ);
    }

    public static Object getOption(String name, Object defaultValue) {

        return getOption(EMPTY_STRING, name, defaultValue);
    }

    public static Object getOption(String owner, String name) {

        return getOption(owner, name, NULL_OBJ);
    }

    public static Object getOption(String owner, String name, Object defaultValue) {

        return getOptionProvider().getOption(owner, name, defaultValue);
    }

    public static Object setOption(String name, Object value) {

        return setOption(EMPTY_STRING, name, value);
    }

    public static Object setOption(String owner, String name, Object value) {

        return getOptionProvider().setOption(owner, name, value);
    }

    public static Object removeOption(String name) {

        return removeOption(EMPTY_STRING, name);
    }

    public static Object removeOption(String owner, String name) {

        return getOptionProvider().removeOption(owner, name);
    }

}

package artoria.data.mask;

import artoria.logging.Logger;
import artoria.logging.LoggerFactory;
import artoria.util.Assert;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Data masking tools.
 * @author Kahle
 */
public class DataMaskUtils {
    private static final Map<String, DataMasker> DATA_MASKER_MAP = new ConcurrentHashMap<String, DataMasker>();
    private static Logger log = LoggerFactory.getLogger(DataMaskUtils.class);

    public static DataMasker unregister(String name) {
        Assert.notBlank(name, "Parameter \"name\" must not blank. ");
        DataMasker remove = DATA_MASKER_MAP.remove(name);
        if (remove != null) {
            String removeClassName = remove.getClass().getName();
            log.info("Unregister \"{}\" to \"{}\". ", removeClassName, name);
        }
        return remove;
    }

    public static void register(String name, DataMasker dataMasker) {
        Assert.notNull(dataMasker, "Parameter \"dataMasker\" must not null. ");
        Assert.notBlank(name, "Parameter \"name\" must not blank. ");
        String dataMaskerClassName = dataMasker.getClass().getName();
        log.info("Register \"{}\" to \"{}\". ", dataMaskerClassName, name);
        DATA_MASKER_MAP.put(name, dataMasker);
    }

    public static String mask(String name, String data) {
        Assert.notBlank(name, "Parameter \"name\" must not blank. ");
        DataMasker dataMasker = DATA_MASKER_MAP.get(name);
        if (dataMasker == null) {
            throw new IllegalStateException(
                    "The data masker named \"" + name + "\" could not be found. "
            );
        }
        return dataMasker.mask(data);
    }

}

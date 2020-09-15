package artoria.storage;

import artoria.logging.Logger;
import artoria.logging.LoggerFactory;
import artoria.util.Assert;

import java.io.File;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class StorageUtils {
    private static final Map<String, StorageProvider> PROVIDER_MAP = new ConcurrentHashMap<String, StorageProvider>();
    private static Logger log = LoggerFactory.getLogger(StorageUtils.class);

    static {

        StorageUtils.register("local", new LocalFileStorageProvider());
    }

    public static StorageProvider unregister(String providerName) {
        Assert.notBlank(providerName, "Parameter \"providerName\" must not blank. ");
        StorageProvider remove = PROVIDER_MAP.remove(providerName);
        if (remove != null) {
            String className = remove.getClass().getName();
            log.info("Unregister \"{}\" to \"{}\". ", className, providerName);
        }
        return remove;
    }

    public static void register(String providerName, StorageProvider storageProvider) {
        Assert.notBlank(providerName, "Parameter \"providerName\" must not blank. ");
        Assert.notNull(storageProvider, "Parameter \"storageProvider\" must not null. ");
        String className = storageProvider.getClass().getName();
        log.info("Register \"{}\" to \"{}\". ", className, providerName);
        PROVIDER_MAP.put(providerName, storageProvider);
    }

    public static StorageProvider getStorageProvider(String providerName) {
        Assert.notBlank(providerName, "Parameter \"providerName\" must not blank. ");
        StorageProvider storageProvider = PROVIDER_MAP.get(providerName);
        Assert.notNull(storageProvider, "The storage provider does not exist. Please register first. ");
        return storageProvider;
    }

    public static StorageObject getObject(String providerName, String containerName, String objectKey) {

        return getStorageProvider(providerName).getObject(containerName, objectKey);
    }

    public static StorageResult putObject(String providerName, String containerName, String objectKey, File file, Map<String, Object> metadata) {

        return getStorageProvider(providerName).putObject(containerName, objectKey, file, metadata);
    }

    public static StorageResult putObject(String providerName, String containerName, String objectKey, InputStream inputStream, Map<String, Object> metadata) {

        return getStorageProvider(providerName).putObject(containerName, objectKey, inputStream, metadata);
    }

}

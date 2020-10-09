package artoria.storage;

import artoria.logging.Logger;
import artoria.logging.LoggerFactory;
import artoria.util.Assert;

import java.io.*;
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

    public static StorageResult putObject(String container, String key, byte[] bytes, Map<String, Object> metadata) {
        StorageProvider storageProvider = getStorageProvider("");
        StorageObject storageObject = new StorageObject();
        storageObject.setContainerName(container);
        storageObject.setObjectKey(key);
        storageObject.setMetadata(metadata);
        storageObject.setObjectContent(new ByteArrayInputStream(bytes));
        return storageProvider.putObject(storageObject);
    }

    public static StorageResult putObject(String container, String key, File file, Map<String, Object> metadata) {
        StorageProvider storageProvider = getStorageProvider("");
        StorageObject storageObject = new StorageObject();
        storageObject.setContainerName(container);
        storageObject.setObjectKey(key);
        storageObject.setMetadata(metadata);
        try {
            storageObject.setObjectContent(new FileInputStream(file));
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return storageProvider.putObject(storageObject);
    }

    public static StorageResult putObject(String container, String key, InputStream in, Map<String, Object> metadata) {
        StorageProvider storageProvider = getStorageProvider("");
        StorageObject storageObject = new StorageObject();
        storageObject.setContainerName(container);
        storageObject.setObjectKey(key);
        storageObject.setMetadata(metadata);
        storageObject.setObjectContent(in);
        return storageProvider.putObject(storageObject);
    }

    public static void deleteObject(String container, String key) {

    }

    public static boolean doesObjectExist(String container, String key) {

        return false;
    }

    public static ListObjectsResult listObjects(String container, String prefix) {

        return null;
    }

    public static Map<String, Object> getMetadata(String container, String key) {

        return null;
    }

    public static StorageObject getObject(String container, String key) {

        return null;
    }

//    public static StorageObject getObject(String providerName, String containerName, String objectKey) {
//
//        return getStorageProvider(providerName).getObject(containerName, objectKey);
//    }

}

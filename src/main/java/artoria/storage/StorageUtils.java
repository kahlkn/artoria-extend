package artoria.storage;

import artoria.logging.Logger;
import artoria.logging.LoggerFactory;
import artoria.util.Assert;
import artoria.util.StringUtils;

import java.io.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static artoria.common.Constants.EMPTY_STRING;

public class StorageUtils {
    private static final Map<String, StorageProvider> PROVIDER_MAP = new ConcurrentHashMap<String, StorageProvider>();
    private static Logger log = LoggerFactory.getLogger(StorageUtils.class);
    private static String defaultProviderName;

    static {
        StorageUtils.register("local", new LocalFileStorageProvider());
        StorageUtils.setDefaultProviderName("local");
    }

    public static void register(String providerName, StorageProvider storageProvider) {
        Assert.notBlank(providerName, "Parameter \"providerName\" must not blank. ");
        Assert.notNull(storageProvider, "Parameter \"storageProvider\" must not null. ");
        String className = storageProvider.getClass().getName();
        log.info("Register \"{}\" to \"{}\". ", className, providerName);
        PROVIDER_MAP.put(providerName, storageProvider);
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

    public static String getDefaultProviderName() {

        return defaultProviderName;
    }

    public static void setDefaultProviderName(String defaultProviderName) {
        Assert.notBlank(defaultProviderName, "Parameter \"defaultProviderName\" must not blank. ");
        log.info("Set default provider name is \"{}\". ", defaultProviderName);
        StorageUtils.defaultProviderName = defaultProviderName;
    }

    public static StorageProvider getStorageProvider(String providerName) {
        if (StringUtils.isBlank(providerName)) {
            String defaultProviderName = getDefaultProviderName();
            Assert.notBlank(defaultProviderName
                    , "Parameter \"providerName\" and the default provider name are both blank. ");
            providerName = defaultProviderName;
        }
        StorageProvider storageProvider = PROVIDER_MAP.get(providerName);
        Assert.notNull(storageProvider, "The storage provider does not exist. Please register first. ");
        return storageProvider;
    }

    public static StorageResult putObject(String providerName, StorageObject storageObject) {

        return getStorageProvider(providerName).putObject(storageObject);
    }

    public static void deleteObject(String providerName, StorageModel storageModel) {

        getStorageProvider(providerName).deleteObject(storageModel);
    }

    public static DeleteObjectsResult deleteObjects(String providerName, DeleteObjectsModel deleteObjectsModel) {

        return getStorageProvider(providerName).deleteObjects(deleteObjectsModel);
    }

    public static boolean doesObjectExist(String providerName, StorageModel storageModel) {

        return getStorageProvider(providerName).doesObjectExist(storageModel);
    }

    public static Map<String, Object> getMetadata(String providerName, StorageModel storageModel) {

        return getStorageProvider(providerName).getMetadata(storageModel);
    }

    public static StorageObject getObject(String providerName, StorageModel storageModel) {

        return getStorageProvider(providerName).getObject(storageModel);
    }

    public static ListObjectsResult listObjects(String providerName, ListObjectsModel listObjectsModel) {

        return getStorageProvider(providerName).listObjects(listObjectsModel);
    }

    public static StorageResult putObject(String bucket, String key, byte[] bytes, Map<String, Object> metadata) {

        return putObject(EMPTY_STRING, bucket, key, bytes, metadata);
    }

    public static StorageResult putObject(String provider, String b, String k, byte[] bytes, Map<String, Object> m) {

        return putObject(provider, b, k, new ByteArrayInputStream(bytes), m);
    }

    public static StorageResult putObject(String bucket, String key, File file, Map<String, Object> metadata) {

        return putObject(EMPTY_STRING, bucket, key, file, metadata);
    }

    public static StorageResult putObject(String provider, String b, String k, File file, Map<String, Object> m) {
        try {
            InputStream inputStream = new FileInputStream(file);
            return putObject(provider, b, k, inputStream, m);
        } catch (IOException e) { throw new StorageException(e); }
    }

    public static StorageResult putObject(String bucket, String key, InputStream in, Map<String, Object> metadata) {

        return putObject(EMPTY_STRING, bucket, key, in, metadata);
    }

    public static StorageResult putObject(String provider, String b, String k, InputStream in, Map<String, Object> m) {
        StorageObject storageObject = new StorageObject(b, k);
        storageObject.setMetadata(m);
        storageObject.setObjectContent(in);
        return getStorageProvider(provider).putObject(storageObject);
    }

    public static void deleteObject(String bucketName, String objectKey) {

        deleteObject(EMPTY_STRING, bucketName, objectKey);
    }

    public static void deleteObject(String providerName, String bucketName, String objectKey) {
        StorageModel storageModel = new StorageModel(bucketName, objectKey);
        getStorageProvider(providerName).deleteObject(storageModel);
    }

    public static boolean doesObjectExist(String bucketName, String objectKey) {

        return doesObjectExist(EMPTY_STRING, bucketName, objectKey);
    }

    public static boolean doesObjectExist(String providerName, String bucketName, String objectKey) {
        StorageModel storageModel = new StorageModel(bucketName, objectKey);
        return getStorageProvider(providerName).doesObjectExist(storageModel);
    }

    public static Map<String, Object> getMetadata(String bucketName, String objectKey) {

        return getMetadata(EMPTY_STRING, bucketName, objectKey);
    }

    public static Map<String, Object> getMetadata(String providerName, String bucketName, String objectKey) {
        StorageModel storageModel = new StorageModel(bucketName, objectKey);
        return getStorageProvider(providerName).getMetadata(storageModel);
    }

    public static StorageObject getObject(String bucketName, String objectKey) {

        return getObject(EMPTY_STRING, bucketName, objectKey);
    }

    public static StorageObject getObject(String providerName, String bucketName, String objectKey) {
        StorageModel storageModel = new StorageModel(bucketName, objectKey);
        return getStorageProvider(providerName).getObject(storageModel);
    }

    public static ListObjectsResult listObjects(String bucketName, String prefix) {

        return listObjects(EMPTY_STRING, bucketName, prefix);
    }

    public static ListObjectsResult listObjects(String providerName, String bucketName, String prefix) {
        ListObjectsModel listObjectsModel = new ListObjectsModel();
        listObjectsModel.setBucketName(bucketName);
        listObjectsModel.setPrefix(prefix);
        return getStorageProvider(providerName).listObjects(listObjectsModel);
    }

}

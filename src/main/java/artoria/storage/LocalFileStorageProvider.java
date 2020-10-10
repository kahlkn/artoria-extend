package artoria.storage;

import artoria.exception.ExceptionUtils;
import artoria.file.FileUtils;
import artoria.file.FilenameUtils;
import artoria.logging.Logger;
import artoria.logging.LoggerFactory;
import artoria.util.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.*;

import static artoria.common.Constants.EMPTY_STRING;

public class LocalFileStorageProvider implements StorageProvider {
    private static Logger log = LoggerFactory.getLogger(LocalFileStorageProvider.class);
    private String defaultBucketName;

    private String bucketName(StorageModel storageModel) {
        String bucketName = storageModel.getBucketName();
        if (StringUtils.isBlank(bucketName)) {
            String defaultBucketName = getDefaultBucketName();
            Assert.notBlank(defaultBucketName, "bucketName and defaultBucketName all is blank. ");
            bucketName = defaultBucketName;
        }
        return bucketName;
    }

    @Override
    public String getDefaultBucketName() {

        return defaultBucketName;
    }

    @Override
    public void setDefaultBucketName(String defaultBucketName) {
        Assert.notBlank(defaultBucketName, "Parameter \"defaultBucketName\" must not blank. ");
        log.info("Set default bucket name is \"{}\". ", defaultBucketName);
        this.defaultBucketName = defaultBucketName;
    }

    @Override
    public StorageResult putObject(StorageObject storageObject) {
        String bucketName = bucketName(storageObject);
        String objectKey = storageObject.getObjectKey();
        InputStream objectContent = storageObject.getObjectContent();
        Map<String, Object> metadata = storageObject.getMetadata();
        FileOutputStream outputStream = null;
        try {
            Assert.notBlank(bucketName, "Parameter \"parameter\" must not blank. ");
            Assert.notBlank(objectKey, "Parameter \"parameter\" must not blank. ");
            Assert.notNull(objectContent, "Parameter \"parameter\" must not blank. ");

            String metadataPath = FilenameUtils.removeExtension(objectKey) + ".metadata";

            FileUtils.write(objectContent, new File(bucketName, objectKey));

            Properties properties = new Properties();
            if (MapUtils.isNotEmpty(metadata)) {
                properties.putAll(metadata);
            }
            outputStream = new FileOutputStream(new File(bucketName, metadataPath));
            properties.store(outputStream, getClass().getName());

            StorageResult storageResult = new StorageResult();
            storageResult.setBucketName(bucketName);
            storageResult.setObjectKey(objectKey);
            return storageResult;
        }
        catch (Exception e) {
            throw ExceptionUtils.wrap(e);
        }
        finally {
            CloseUtils.closeQuietly(objectContent);
            CloseUtils.closeQuietly(outputStream);
        }
    }

    @Override
    public void deleteObject(StorageModel storageModel) {
        String bucketName = bucketName(storageModel);
        String objectKey = storageModel.getObjectKey();
        File file = new File(bucketName, objectKey);
        file.deleteOnExit();
    }

    @Override
    public DeleteObjectsResult deleteObjects(DeleteObjectsModel deleteObjectsModel) {
        String bucketName = bucketName(deleteObjectsModel);
        List<String> objectKeys = deleteObjectsModel.getObjectKeys();
        List<String> deletedObjectKeys = new ArrayList<String>();
        for (String objectKey : objectKeys) {
            try {
                File file = new File(bucketName, objectKey);
                file.deleteOnExit();
                deletedObjectKeys.add(objectKey);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        DeleteObjectsResult deleteObjectsResult = new DeleteObjectsResult();
        deleteObjectsResult.setDeletedObjectKeys(deletedObjectKeys);
        return deleteObjectsResult;
    }

    @Override
    public boolean doesObjectExist(StorageModel storageModel) {
        String bucketName = bucketName(storageModel);
        String objectKey = storageModel.getObjectKey();
        return new File(bucketName, objectKey).exists();
    }

    @Override
    public Map<String, Object> getMetadata(StorageModel storageModel) {
        String bucketName = bucketName(storageModel);
        String objectKey = storageModel.getObjectKey();
        InputStream metadataInputStream = null;
        try {
            Assert.notBlank(objectKey, "Parameter \"parameter\" must not blank. ");

            String metadataPath = FilenameUtils.removeExtension(objectKey) + ".metadata";

            metadataInputStream = new FileInputStream(new File(bucketName, metadataPath));
            Map<String, Object> metadata = new LinkedHashMap<String, Object>();
            Properties properties = new Properties();
            properties.load(metadataInputStream);
            Enumeration<?> enumeration = properties.propertyNames();
            while (enumeration.hasMoreElements()) {
                Object nextElement = enumeration.nextElement();
                if (nextElement == null) { continue; }
                String key = String.valueOf(nextElement);
                String val = properties.getProperty(key);
                metadata.put(key, val);
            }

            return metadata;
        }
        catch (Exception e) {
            throw ExceptionUtils.wrap(e);
        }
        finally {
            CloseUtils.closeQuietly(metadataInputStream);
        }
    }

    @Override
    public StorageObject getObject(StorageModel storageModel) {
        String bucketName = bucketName(storageModel);
        String objectKey = storageModel.getObjectKey();
        InputStream metadataInputStream = null;
        try {
            Assert.notBlank(objectKey, "Parameter \"parameter\" must not blank. ");

            String metadataPath = FilenameUtils.removeExtension(objectKey) + ".metadata";

            InputStream inputStream = new FileInputStream(new File(bucketName, objectKey));

            metadataInputStream = new FileInputStream(new File(bucketName, metadataPath));
            Map<String, Object> metadata = new LinkedHashMap<String, Object>();
            Properties properties = new Properties();
            properties.load(metadataInputStream);
            Enumeration<?> enumeration = properties.propertyNames();
            while (enumeration.hasMoreElements()) {
                Object nextElement = enumeration.nextElement();
                if (nextElement == null) { continue; }
                String key = String.valueOf(nextElement);
                String val = properties.getProperty(key);
                metadata.put(key, val);
            }

            StorageObject storageObject = new StorageObject();
            storageObject.setBucketName(bucketName);
            storageObject.setObjectKey(objectKey);
            storageObject.setObjectContent(inputStream);
            storageObject.setMetadata(metadata);
            return storageObject;
        }
        catch (Exception e) {
            throw ExceptionUtils.wrap(e);
        }
        finally {
            CloseUtils.closeQuietly(metadataInputStream);
        }
    }

    @Override
    public ListObjectsResult listObjects(ListObjectsModel listObjectsModel) {
        String bucketName = bucketName(listObjectsModel);
        String prefix = listObjectsModel.getPrefix();
        File file = new File(bucketName, prefix);
        File[] listFiles = file.listFiles();
        List<StorageObject> objects = new ArrayList<StorageObject>();
        if (ArrayUtils.isNotEmpty(listFiles)) {
            String bucketNameNew = new File(bucketName).toString();
            String prefixNew = null;
            if (StringUtils.isNotBlank(prefix)) {
                prefixNew = prefix.replaceAll("\\\\", "/");
                prefixNew = prefixNew.startsWith("/") ? prefix : "/" + prefix;
            }
            for (File listFile : listFiles) {
                if (listFile.isDirectory()) { continue; }
                String listFileStr = listFile.toString();
                listFileStr = listFileStr.replace(bucketNameNew, EMPTY_STRING);
                listFileStr = listFileStr.replaceAll("\\\\", "/");
                listFileStr = listFileStr.startsWith("/") ? listFileStr : "/" + listFileStr;
                if (StringUtils.isNotBlank(prefixNew) && !listFileStr.startsWith(prefixNew)) {
                    continue;
                }
                StorageObject storageObject = new StorageObject(bucketName, listFileStr);
                objects.add(storageObject);
            }
        }
        ListObjectsResult listObjectsResult = new ListObjectsResult();
        listObjectsResult.setBucketName(bucketName);
        listObjectsResult.setPrefix(prefix);
        listObjectsResult.setObjects(objects);
        return listObjectsResult;
    }

}

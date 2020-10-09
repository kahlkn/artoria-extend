package artoria.storage;

import artoria.exception.ExceptionUtils;
import artoria.file.FileUtils;
import artoria.file.FilenameUtils;
import artoria.util.Assert;
import artoria.util.CloseUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

public class LocalFileStorageProvider implements StorageProvider {

    public StorageObject getObject(String containerName, String objectKey) {
        InputStream metadataInputStream = null;
        try {
            Assert.notBlank(containerName, "Parameter \"parameter\" must not blank. ");
            Assert.notBlank(objectKey, "Parameter \"parameter\" must not blank. ");

            String metadataPath = FilenameUtils.removeExtension(objectKey) + ".metadata";

            InputStream inputStream = new FileInputStream(new File(containerName, objectKey));

            metadataInputStream = new FileInputStream(new File(containerName, metadataPath));
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
            storageObject.setBucketName(containerName);
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

    public StorageResult putObject(String containerName, String objectKey, InputStream inputStream, Map<String, Object> metadata) {
        FileOutputStream outputStream = null;
        try {
            Assert.notBlank(containerName, "Parameter \"parameter\" must not blank. ");
            Assert.notBlank(objectKey, "Parameter \"parameter\" must not blank. ");
            Assert.notNull(inputStream, "Parameter \"parameter\" must not blank. ");

            String metadataPath = FilenameUtils.removeExtension(objectKey) + ".metadata";

            FileUtils.write(inputStream, new File(containerName, objectKey));

            Properties properties = new Properties();
            properties.putAll(metadata);
            outputStream = new FileOutputStream(new File(containerName, metadataPath));
            properties.store(outputStream, getClass().getName());

            StorageResult storageResult = new StorageResult();
            return storageResult;
        }
        catch (Exception e) {
            throw ExceptionUtils.wrap(e);
        }
        finally {
            CloseUtils.closeQuietly(inputStream);
            CloseUtils.closeQuietly(outputStream);
        }
    }

    @Override
    public String getDefaultBucketName() {
        return null;
    }

    @Override
    public void setDefaultBucketName(String defaultBucketName) {

    }

    @Override
    public StorageResult putObject(StorageObject storageObject) {
        return null;
    }

    @Override
    public void deleteObject(StorageModel storageModel) {

    }

    @Override
    public DeleteObjectsResult deleteObjects(DeleteObjectsModel deleteObjectsModel) {
        return null;
    }

    @Override
    public boolean doesObjectExist(StorageModel storageModel) {
        return false;
    }

    @Override
    public Map<String, Object> getMetadata(StorageModel storageModel) {
        return null;
    }

    @Override
    public StorageObject getObject(StorageModel storageModel) {
        return null;
    }

    @Override
    public ListObjectsResult listObjects(ListObjectsModel listObjectsModel) {
        return null;
    }

}

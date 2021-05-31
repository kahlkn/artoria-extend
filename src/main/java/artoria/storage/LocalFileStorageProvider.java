package artoria.storage;

import artoria.exception.ExceptionUtils;
import artoria.file.FileUtils;
import artoria.file.FilenameUtils;
import artoria.logging.Logger;
import artoria.logging.LoggerFactory;
import artoria.time.DateUtils;
import artoria.util.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.*;

import static artoria.common.Constants.*;

public class LocalFileStorageProvider implements StorageProvider {
    private static final String METADATA_SUFFIX = ".metadata";
    private static final String COMMENTS = "The provider is \"" + LocalFileStorageProvider.class.getName() + "\". ";
    private static Logger log = LoggerFactory.getLogger(LocalFileStorageProvider.class);

    private Properties getMetadataProperties(String bucketName, String objectKey) {
        int lastSeparator = FilenameUtils.indexOfLastSeparator(objectKey);
        int length = objectKey.length();
        String metadataPath = objectKey.substring(0, lastSeparator) + "/" + ".metadata" + objectKey.substring(lastSeparator, length) + METADATA_SUFFIX;
//        String metadataPath = objectKey + METADATA_SUFFIX;
        File metadataFile = new File(bucketName, metadataPath);
        if (!metadataFile.exists() ||
                metadataFile.isDirectory()) {
            return null;
        }
        InputStream metadataInputStream = null;
        try {
            metadataInputStream = new FileInputStream(metadataFile);
            Properties properties = new Properties();
            properties.load(metadataInputStream);
            return properties;
        }
        catch (Exception e) {
            throw ExceptionUtils.wrap(e);
        }
        finally {
            CloseUtils.closeQuietly(metadataInputStream);
        }
    }

    private Properties initMetadataProperties(Properties properties) {
        boolean isNew = properties == null;
        if (isNew) { properties = new Properties(); }
        String timestamp = String.valueOf(DateUtils.getTimeInMillis());
        // Creation time.
        String key = "creation-time";
        if (!properties.containsKey(key)) {
            properties.setProperty(key, isNew ? timestamp : ZERO_STR);
        }
        // Last modified time.
        key = "last-modified-time";
        properties.setProperty(key, timestamp);
        return properties;
    }

    @Override
    public ObjectResult putObject(StorageObject storageObject) {
        Assert.notNull(storageObject, "Parameter \"storageObject\" must not null. ");
        InputStream objectContent = storageObject.getObjectContent();
        Map<String, Object> metadata = storageObject.getMetadata();
        String bucketName = storageObject.getBucketName();
        String objectKey = storageObject.getObjectKey();
        Assert.notNull(objectContent, "Parameter \"objectContent\" must not null. ");
        Assert.notBlank(bucketName, "Parameter \"bucketName\" must not blank. ");
        Assert.notBlank(objectKey, "Parameter \"objectKey\" must not blank. ");
        FileOutputStream outputStream = null;
        try {
            //
            File file = new File(bucketName, objectKey);
            FileUtils.write(objectContent, file);

            Properties properties = getMetadataProperties(bucketName, objectKey);
            properties = initMetadataProperties(properties);

            if (MapUtils.isNotEmpty(metadata)) {
                properties.putAll(metadata);
            }

//            Long lastModifiedTime = Long.valueOf(properties.getProperty("last-modified-time"));
//            boolean b = file.setLastModified(lastModifiedTime);

//            String metadataPath = objectKey + METADATA_SUFFIX;
            int lastSeparator = FilenameUtils.indexOfLastSeparator(objectKey);
            int length = objectKey.length();
            String metadataPath = objectKey.substring(0, lastSeparator) + "/" + ".metadata" + objectKey.substring(lastSeparator, length) + METADATA_SUFFIX;

            File metadataFile = new File(bucketName, metadataPath);
            log.info(">> {}", metadataFile);
            File parentFile = metadataFile.getParentFile();
            if (!parentFile.exists()) {
                boolean mkdirs = parentFile.mkdirs();
            }
            outputStream = new FileOutputStream(metadataFile);
            properties.store(outputStream, COMMENTS);

            ObjectResult objectResult = new ObjectResult();
            objectResult.setBucketName(bucketName);
            objectResult.setObjectKey(objectKey);
            return objectResult;
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
    public void deleteObject(ObjectModel objectModel) {
        Assert.notNull(objectModel, "Parameter \"objectModel\" must not null. ");
        String bucketName = objectModel.getBucketName();
        String objectKey = objectModel.getObjectKey();
        Assert.notBlank(bucketName, "Parameter \"bucketName\" must not blank. ");
        Assert.notBlank(objectKey, "Parameter \"objectKey\" must not blank. ");
        File file = new File(bucketName, objectKey);
        boolean delete = file.delete();
    }

    @Override
    public DeleteObjectsResult deleteObjects(DeleteObjectsModel deleteObjectsModel) {
        Assert.notNull(deleteObjectsModel, "Parameter \"deleteObjectsModel\" must not null. ");
        List<String> objectKeys = deleteObjectsModel.getObjectKeys();
        String bucketName = deleteObjectsModel.getBucketName();
        Assert.notBlank(bucketName, "Parameter \"bucketName\" must not blank. ");
        Assert.notEmpty(objectKeys, "Parameter \"objectKeys\" must not empty. ");
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
    public boolean doesObjectExist(ObjectModel objectModel) {
        Assert.notNull(objectModel, "Parameter \"objectModel\" must not null. ");
        String bucketName = objectModel.getBucketName();
        String objectKey = objectModel.getObjectKey();
        Assert.notBlank(bucketName, "Parameter \"bucketName\" must not blank. ");
        Assert.notBlank(objectKey, "Parameter \"objectKey\" must not blank. ");
        return new File(bucketName, objectKey).exists();
    }

    @Override
    public Map<String, Object> getMetadata(ObjectModel objectModel) {
        Assert.notNull(objectModel, "Parameter \"objectModel\" must not null. ");
        String bucketName = objectModel.getBucketName();
        String objectKey = objectModel.getObjectKey();
        Assert.notBlank(bucketName, "Parameter \"bucketName\" must not blank. ");
        Assert.notBlank(objectKey, "Parameter \"objectKey\" must not blank. ");
        Properties properties = getMetadataProperties(bucketName, objectKey);
        Map<String, Object> metadata = new LinkedHashMap<String, Object>();
        if (properties == null || properties.isEmpty()) { return metadata; }
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

    @Override
    public StorageObject getObject(ObjectModel objectModel) {
        Assert.notNull(objectModel, "Parameter \"objectModel\" must not null. ");
        String bucketName = objectModel.getBucketName();
        String objectKey = objectModel.getObjectKey();
        Assert.notBlank(bucketName, "Parameter \"bucketName\" must not blank. ");
        Assert.notBlank(objectKey, "Parameter \"objectKey\" must not blank. ");
        try {
            InputStream inputStream = new FileInputStream(new File(bucketName, objectKey));

            Properties properties = getMetadataProperties(bucketName, objectKey);
            Map<String, Object> metadata = new LinkedHashMap<String, Object>();
            if (properties != null && !properties.isEmpty()) {
                Enumeration<?> enumeration = properties.propertyNames();
                while (enumeration.hasMoreElements()) {
                    Object nextElement = enumeration.nextElement();
                    if (nextElement == null) { continue; }
                    String key = String.valueOf(nextElement);
                    String val = properties.getProperty(key);
                    metadata.put(key, val);
                }
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
    }

    @Override
    public ListObjectsResult listObjects(ListObjectsModel listObjectsModel) {
        Assert.notNull(listObjectsModel, "Parameter \"listObjectsModel\" must not null. ");
        String bucketName = listObjectsModel.getBucketName();
        String delimiter = listObjectsModel.getDelimiter();
        String prefix = listObjectsModel.getPrefix();
        Assert.notBlank(bucketName, "Parameter \"bucketName\" must not blank. ");
        if (StringUtils.isBlank(delimiter)) { delimiter = SLASH; }
        if (StringUtils.isNotBlank(prefix) && !prefix.endsWith(SLASH)) {
            throw new StorageException("Parameter \"prefix\" must end With \"/\". ");
        }
        bucketName = bucketName.replaceAll("\\\\", SLASH);
        bucketName = bucketName.endsWith(SLASH) ? bucketName : bucketName + SLASH;
        List<StorageObject> objectSummaries = new ArrayList<StorageObject>();
        List<String> commonPrefixes = new ArrayList<String>();
        ListObjectsResult listObjectsResult = new ListObjectsResult();
        listObjectsResult.setObjectSummaries(objectSummaries);
        listObjectsResult.setCommonPrefixes(commonPrefixes);
        listObjectsResult.setBucketName(bucketName);
        listObjectsResult.setDelimiter(delimiter);
        listObjectsResult.setPrefix(prefix);
        listObjectsResult.setMarker(listObjectsModel.getMarker());
        listObjectsResult.setMaxKeys(listObjectsModel.getMaxKeys());
        //listObjectsResult.setTruncated();
        //listObjectsResult.setNextMarker();
        File filePath = new File(bucketName, prefix);
        File[] files = filePath.listFiles();
        if (ArrayUtils.isEmpty(files)) { return listObjectsResult; }
        String bucketNameNew = new File(bucketName).toString();
        String prefixNew = null;
        if (StringUtils.isNotBlank(prefix)) {
            prefixNew = prefix.replaceAll("\\\\", SLASH);
            prefixNew = prefixNew.startsWith(SLASH) ? prefixNew.substring(ONE) : prefix;
        }
        for (File file : files) {
            if (file == null) { continue; }
            String fileStr = file.toString();
            fileStr = fileStr.replace(bucketNameNew, EMPTY_STRING);
            fileStr = fileStr.replaceAll("\\\\", SLASH);
            fileStr = fileStr.startsWith(SLASH) ? fileStr.substring(ONE) : fileStr;
            if (StringUtils.isNotBlank(prefixNew) && !fileStr.startsWith(prefixNew)) {
                continue;
            }
            if (file.isDirectory()) {
                fileStr = fileStr.endsWith(SLASH) ? fileStr : fileStr + SLASH;
                commonPrefixes.add(fileStr);
            }
            else {
                StorageObject storageObject = new StorageObject(bucketName, fileStr);
                objectSummaries.add(storageObject);
            }
        }
        return listObjectsResult;
    }

}

package artoria.storage;

import artoria.exception.ExceptionUtils;
import artoria.file.FileUtils;
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
    private static Logger log = LoggerFactory.getLogger(LocalFileStorageProvider.class);

    @Override
    public StorageResult putObject(StorageObject storageObject) {
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
            String metadataPath = objectKey + METADATA_SUFFIX;
            File file = new File(bucketName, objectKey);
            FileUtils.write(objectContent, file);
            Properties properties = new Properties();
            if (MapUtils.isNotEmpty(metadata)) {
                properties.putAll(metadata);
            }
            //
            String timestamp = String.valueOf(DateUtils.getTimestamp());
            // creationTime
            if (!properties.containsKey("creation-time")) {
                properties.setProperty("creation-time", timestamp);
            }
            // lastModifiedTime
            if (!properties.containsKey("last-modified-time")) {
                properties.setProperty("last-modified-time", timestamp);
            }
            Long lastModifiedTime = Long.valueOf(properties.getProperty("last-modified-time"));
            boolean b = file.setLastModified(lastModifiedTime);

            outputStream = new FileOutputStream(new File(bucketName, metadataPath));
            properties.store(outputStream, EMPTY_STRING);

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
        Assert.notNull(storageModel, "Parameter \"storageModel\" must not null. ");
        String bucketName = storageModel.getBucketName();
        String objectKey = storageModel.getObjectKey();
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
    public boolean doesObjectExist(StorageModel storageModel) {
        Assert.notNull(storageModel, "Parameter \"storageModel\" must not null. ");
        String bucketName = storageModel.getBucketName();
        String objectKey = storageModel.getObjectKey();
        Assert.notBlank(bucketName, "Parameter \"bucketName\" must not blank. ");
        Assert.notBlank(objectKey, "Parameter \"objectKey\" must not blank. ");
        return new File(bucketName, objectKey).exists();
    }

    @Override
    public Map<String, Object> getMetadata(StorageModel storageModel) {
        Assert.notNull(storageModel, "Parameter \"storageModel\" must not null. ");
        String bucketName = storageModel.getBucketName();
        String objectKey = storageModel.getObjectKey();
        Assert.notBlank(bucketName, "Parameter \"bucketName\" must not blank. ");
        Assert.notBlank(objectKey, "Parameter \"objectKey\" must not blank. ");
        InputStream metadataInputStream = null;
        try {
            String metadataPath = objectKey + METADATA_SUFFIX;

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
        Assert.notNull(storageModel, "Parameter \"storageModel\" must not null. ");
        String bucketName = storageModel.getBucketName();
        String objectKey = storageModel.getObjectKey();
        Assert.notBlank(bucketName, "Parameter \"bucketName\" must not blank. ");
        Assert.notBlank(objectKey, "Parameter \"objectKey\" must not blank. ");
        InputStream metadataInputStream = null;
        try {
            String metadataPath = objectKey + ".metadata";

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

package artoria.storage;

import java.util.Map;

public interface StorageProvider {

    String getDefaultBucketName();

    void setDefaultBucketName(String defaultBucketName);

    StorageResult putObject(StorageObject storageObject);

    void deleteObject(StorageModel storageModel);

    DeleteObjectsResult deleteObjects(DeleteObjectsModel deleteObjectsModel);

    boolean doesObjectExist(StorageModel storageModel);

    Map<String, Object> getMetadata(StorageModel storageModel);

    StorageObject getObject(StorageModel storageModel);

    ListObjectsResult listObjects(ListObjectsModel listObjectsModel);

}

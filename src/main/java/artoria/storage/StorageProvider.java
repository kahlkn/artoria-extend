package artoria.storage;

import java.util.Map;

public interface StorageProvider {

    ObjectResult putObject(StorageObject storageObject);

    void deleteObject(ObjectModel objectModel);

    DeleteObjectsResult deleteObjects(DeleteObjectsModel deleteObjectsModel);

    boolean doesObjectExist(ObjectModel objectModel);

    Map<String, Object> getMetadata(ObjectModel objectModel);

    StorageObject getObject(ObjectModel objectModel);

    ListObjectsResult listObjects(ListObjectsModel listObjectsModel);

    // CopyObjectResult copyObject(CopyObjectModel copyObjectModel);

}

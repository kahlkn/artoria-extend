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

    /*
    AppendObject
    CopyObject
    moveObject

    GetObjectMeta
    PutObjectMeta

    ----
    PutObjectResult putObject(PutObjectRequest putObjectRequest)

    void deleteObject(GenericRequest genericRequest)

    DeleteObjectsResult deleteObjects(DeleteObjectsRequest deleteObjectsRequest)

    boolean doesObjectExist(GenericRequest genericRequest)

    ObjectMetadata getObjectMetadata(GenericRequest genericRequest)

    SimplifiedObjectMeta getSimplifiedObjectMeta(GenericRequest genericRequest)

    OSSObject getObject(GetObjectRequest getObjectRequest)

    ObjectListing listObjects(ListObjectsRequest listObjectsRequest);

    CopyObjectResult copyObject(CopyObjectRequest copyObjectRequest)

    * */


}

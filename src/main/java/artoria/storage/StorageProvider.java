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

    /*

    limit

    ----
    PutObject
    DeleteObject
    DeleteMultipleObjects
    doesObjectExist
    GetObject
    listObjects

    AppendObject
    CopyObject
    moveObject

    HeadObject   全部元信息 大概率是全部元信息（自己发挥咯）
    GetObjectMeta   基本元信息 大概率是全部元信息（自己发挥咯）
    PutObjectMeta

    * */


}

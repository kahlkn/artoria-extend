package artoria.storage;

import java.io.File;
import java.io.InputStream;
import java.util.Map;

public interface StorageProvider {

    StorageObject getObject(String containerName, String objectKey);

    StorageResult putObject(String containerName, String objectKey, File file, Map<String, Object> metadata);

    StorageResult putObject(String containerName, String objectKey, InputStream inputStream, Map<String, Object> metadata);

    void deleteObject(String bucketName, String key);

}

package artoria.storage;

import java.io.InputStream;
import java.util.Map;

/**
 * Object in the object storage.
 * @author Kahle
 */
public class StorageObject extends StorageModel {
    /**
     * Object's metadata.
     */
    private Map<String, Object> metadata;
    /**
     * Object's content.
     */
    private InputStream objectContent;

    public StorageObject() {
    }

    public StorageObject(String bucketName, String objectKey) {

        super(bucketName, objectKey);
    }

    public Map<String, Object> getMetadata() {

        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {

        this.metadata = metadata;
    }

    public InputStream getObjectContent() {

        return objectContent;
    }

    public void setObjectContent(InputStream objectContent) {

        this.objectContent = objectContent;
    }

}

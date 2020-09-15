package artoria.storage;

import java.io.InputStream;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Object in the object storage.
 * @author Kahle
 */
public class StorageObject implements Serializable {
    /**
     * Object's bucket name.containerName
     */
    private String containerName;
    /**
     * Object key (name).
     */
    private String objectKey;
    /**
     * Object's content.
     */
    private InputStream objectContent;
    /**
     * Object's metadata.
     */
    private Map<String, Object> metadata = new LinkedHashMap<String, Object>();

    public String getContainerName() {
        return containerName;
    }

    public void setContainerName(String containerName) {
        this.containerName = containerName;
    }

    public String getObjectKey() {

        return objectKey;
    }

    public void setObjectKey(String objectKey) {

        this.objectKey = objectKey;
    }

    public InputStream getObjectContent() {

        return objectContent;
    }

    public void setObjectContent(InputStream objectContent) {

        this.objectContent = objectContent;
    }

    public Map<String, Object> getMetadata() {

        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {

        this.metadata = metadata;
    }

}

package artoria.storage;

import artoria.data.AbstractExtraData;

import java.io.Serializable;

public class StorageModel extends AbstractExtraData implements Serializable {
    /**
     * Object's bucket name.
     */
    private String bucketName;
    /**
     * Object key (name).
     */
    private String objectKey;

    public StorageModel() {
    }

    public StorageModel(String bucketName, String objectKey) {
        this.bucketName = bucketName;
        this.objectKey = objectKey;
    }

    public String getBucketName() {

        return bucketName;
    }

    public void setBucketName(String bucketName) {

        this.bucketName = bucketName;
    }

    public String getObjectKey() {

        return objectKey;
    }

    public void setObjectKey(String objectKey) {

        this.objectKey = objectKey;
    }

}

package artoria.storage;

import artoria.data.AbstractExtraData;

import java.io.Serializable;

/**
 * The stored result of the object.
 * @author Kahle
 */
public class ObjectResult extends AbstractExtraData implements Serializable {
    /**
     * Object's bucket name.
     */
    private String bucketName;
    /**
     * Object key (name).
     */
    private String objectKey;

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

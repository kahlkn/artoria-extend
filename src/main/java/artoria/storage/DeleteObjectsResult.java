package artoria.storage;

import java.util.List;

public class DeleteObjectsResult extends StorageResult {
    /**
     * Successfully deleted objects.
     */
    private List<String> deletedObjectKeys;

    public List<String> getDeletedObjectKeys() {

        return deletedObjectKeys;
    }

    public void setDeletedObjectKeys(List<String> deletedObjectKeys) {

        this.deletedObjectKeys = deletedObjectKeys;
    }

}

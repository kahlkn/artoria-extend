package artoria.storage;

import java.util.List;

public class ListObjectsResult extends StorageResult {
    /**
     * A list of summary information describing the objects stored in the container.
     */
    private List<StorageObject> objects;
    /**
     * The prefix filter.
     */
    private String prefix;

    public List<StorageObject> getObjects() {

        return objects;
    }

    public void setObjects(List<StorageObject> objects) {

        this.objects = objects;
    }

    public String getPrefix() {

        return prefix;
    }

    public void setPrefix(String prefix) {

        this.prefix = prefix;
    }

}

package artoria.storage;

public class ListObjectsModel extends StorageModel {
    /**
     * The prefix filter -- objects returned whose key must start with this prefix.
     */
    private String prefix;

    public String getPrefix() {

        return prefix;
    }

    public void setPrefix(String prefix) {

        this.prefix = prefix;
    }

}

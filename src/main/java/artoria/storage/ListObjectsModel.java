package artoria.storage;

public class ListObjectsModel extends ObjectModel {
    /**
     * The objects returned whose key must start with this prefix.
     */
    private String prefix;
    /**
     * The objects returned whose key must be greater than the maker in lexicographical order.
     */
    private String marker;
    /**
     * The delimiters of object names returned.
     */
    private String delimiter;
    /**
     * The max objects to return.
     */
    private Integer maxKeys;

    public String getPrefix() {

        return prefix;
    }

    public void setPrefix(String prefix) {

        this.prefix = prefix;
    }

    public String getMarker() {

        return marker;
    }

    public void setMarker(String marker) {

        this.marker = marker;
    }

    public String getDelimiter() {

        return delimiter;
    }

    public void setDelimiter(String delimiter) {

        this.delimiter = delimiter;
    }

    public Integer getMaxKeys() {

        return maxKeys;
    }

    public void setMaxKeys(Integer maxKeys) {

        this.maxKeys = maxKeys;
    }

}

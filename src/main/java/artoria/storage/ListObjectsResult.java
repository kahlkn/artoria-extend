package artoria.storage;

import java.util.List;

public class ListObjectsResult extends StorageResult {
    /**
     * A list of summary information describing the objects stored in the container.
     */
    private List<StorageObject> objectSummaries;
    private List<String> commonPrefixes;
    private String prefix;
    private String marker;
    private String delimiter;
    private Integer maxKeys;
    private Boolean truncated;
    private String nextMarker;

    public List<StorageObject> getObjectSummaries() {

        return objectSummaries;
    }

    public void setObjectSummaries(List<StorageObject> objectSummaries) {

        this.objectSummaries = objectSummaries;
    }

    public List<String> getCommonPrefixes() {

        return commonPrefixes;
    }

    public void setCommonPrefixes(List<String> commonPrefixes) {

        this.commonPrefixes = commonPrefixes;
    }

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

    public Boolean getTruncated() {

        return truncated;
    }

    public void setTruncated(Boolean truncated) {

        this.truncated = truncated;
    }

    public String getNextMarker() {

        return nextMarker;
    }

    public void setNextMarker(String nextMarker) {

        this.nextMarker = nextMarker;
    }

}

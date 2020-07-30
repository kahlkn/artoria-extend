package artoria.data.mask;

/**
 * Provide a high-level abstract of the data masker tools.
 * @author Kahle
 */
public interface DataMasker {

    /**
     * Data masking.
     * @param data Data to be masked
     * @return Masked results
     */
    String mask(String data);

}

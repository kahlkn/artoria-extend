package artoria.storage;

import artoria.exception.UncheckedException;

/**
 * Storage exception.
 * @author Kahle
 */
public class StorageException extends UncheckedException {

    public StorageException() {

        super();
    }

    public StorageException(String message) {

        super(message);
    }

    public StorageException(Throwable cause) {

        super(cause);
    }

    public StorageException(String message, Throwable cause) {

        super(message, cause);
    }

}

package artoria.mail;

import artoria.exception.UncheckedException;

public class MailException extends UncheckedException {

    public MailException() {

        super();
    }

    public MailException(String message) {

        super(message);
    }

    public MailException(Throwable cause) {

        super(cause);
    }

    public MailException(String message, Throwable cause) {

        super(message, cause);
    }

}

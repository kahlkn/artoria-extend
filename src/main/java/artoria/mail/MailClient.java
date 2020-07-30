package artoria.mail;

import java.util.List;

public interface MailClient {

    void send(Mail... mails);

    Integer getTotalMailCount(String folderName);

    Integer getNewMailCount(String folderName);

    Integer getUnreadMailCount(String folderName);

    Integer getDeletedMailCount(String folderName);

    Mail getMail(String folderName, Integer number);

    List<Mail> getMails(String folderName, Integer startNumber, Integer endNumber);

}

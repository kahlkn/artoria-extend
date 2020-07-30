package artoria.mail;

import com.alibaba.fastjson.JSON;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.util.Properties;

@Ignore
public class JavaMailClientTest {
    private static Properties mailProperties = new Properties();
    private static MailClient mailClient;

    static {
        mailProperties.setProperty("mail.smtp.host", "smtp.qq.com");
        mailProperties.setProperty("mail.smtp.port", "465");
        mailProperties.setProperty("mail.smtp.ssl.enable", "true");
        mailProperties.setProperty("mail.transport.protocol", "smtp");

        mailProperties.setProperty("mail.imap.host", "imap.qq.com");
        mailProperties.setProperty("mail.imap.port", "993");
        mailProperties.setProperty("mail.imap.ssl.enable", "true");
        mailProperties.setProperty("mail.pop3.host", "");
        mailProperties.setProperty("mail.pop3.port", "");
        mailProperties.setProperty("mail.pop3.ssl.enable", "");
        mailProperties.setProperty("mail.store.protocol", "imap");

        mailProperties.setProperty("mail.debug", "true");

        String username = "username@mail.com";
        String password = "password";
        mailClient = new JavaMailClient(mailProperties, username, password);
    }

    @Test
    public void test1() {
        Mail mailMessage = new SimpleMail();
//        mailMessage.setFrom("test<test@mail.com>");
        mailMessage.setTo("test1<test1@mail.com>,test2<test2@mail.com>");
        mailMessage.setSubject("Mail Test Test");
        mailMessage.setContent("<h1>Mail Test</h1><p>This is a test mail. </p>");
        File file = new File("E:\\Temp\\test.jpg");
        mailMessage.addAttachment("test.jpg", file);
        mailClient.send(mailMessage);
    }

    @Test
    public void test2() throws Exception {
        // INBOX , Sent Messages , Drafts , Deleted Messages , Junk
        // JavaMailClient mailClient = (JavaMailClient) JavaMailClientTest.mailClient;
        Integer totalMailCount = mailClient.getTotalMailCount("INBOX");
        System.out.println(totalMailCount);
        System.out.println(JSON.toJSONString(mailClient.getMail("INBOX", totalMailCount)));
//        List<Mail> mailList = mailClient.getMails("INBOX", 1, count);
//        for (Mail mail : mailList) {
//            System.out.println(JSON.toJSONString(mail));
//        }
    }

}

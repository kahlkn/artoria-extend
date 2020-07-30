package artoria.mail;

import artoria.exception.ExceptionUtils;
import artoria.io.IOUtils;
import artoria.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.*;
import java.util.*;

import static artoria.common.Constants.DEFAULT_ENCODING_NAME;
import static artoria.common.Constants.ZERO;
import static artoria.util.ObjectUtils.cast;
import static java.lang.Boolean.TRUE;
import static javax.mail.Flags.Flag.SEEN;
import static javax.mail.Message.RecipientType.*;

public class JavaMailClient implements MailClient {
    // https://javaee.github.io/javamail/docs/api/com/sun/mail/smtp/package-summary.html
    private static final String INBOX = "INBOX";
    private static final String APPLICATION_ALL = "application/*";
    private static final String MESSAGE_RFC822 = "message/rfc822";
    private static final String MULTIPART_ALL = "multipart/*";
    private static final String X_PRIORITY = "X-Priority";
    private static final String STRING_NAME = "name";
    private static final String TEXT_ALL = "text/*";
    private static final String EMAIL_CONFIG_NAME = "mail.properties";
    private static Logger log = LoggerFactory.getLogger(JavaMailClient.class);
    private final Properties properties;
    private final String username;
    private final String password;
    private Session session;
    private Store store;

    public JavaMailClient(Properties properties) {

        this(properties, null, null);
    }

    public JavaMailClient(Properties properties, String username, String password) {
        Assert.notNull(properties, "Parameter \"properties\" must not null. ");
        this.properties = properties;
        this.username = username;
        this.password = password;
    }

    private void closeQuietly(Folder folder, boolean expunge) {
        if (folder != null && folder.isOpen()) {
            try {
                folder.close(expunge);
            }
            catch (MessagingException e) {
                log.error(e.getMessage()/*todo */, e);
            }
        }
    }

    private void closeQuietly(Store store) {
        if (store != null && store.isConnected()) {
            try {
                store.close();
            }
            catch (MessagingException e) {
                log.error(e.getMessage()/*todo */, e);
            }
        }
    }

    private Session session() {
        if (session != null) { return session; }
        synchronized (this) {
            if (session != null) { return session; }
            // Don't use getDefaultInstance.
            session = Session.getInstance(properties);
        }
        return session;
    }

    private Store store() {
        if (store != null) { return store; }
        try {
            synchronized (this) {
                if (store != null) { return store; }
                store = session().getStore();
                store.connect(username, password);
            }
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new MailException(e);
        }
        return store;
    }

    public Folder[] getAllFolders(Store store) {
        try {
            return store.getDefaultFolder().list();
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new MailException(e);
        }
    }

    public Folder getInbox() {

        return getFolder(INBOX);
    }

    public Folder getInbox(boolean readOnly) {

        return getFolder(INBOX, readOnly);
    }

    public Folder getFolder(String folderName) {

        return getFolder(folderName, TRUE);
    }

    public Folder getFolder(String folderName, boolean readOnly) {
        try {
            Folder folder = store().getFolder(folderName);
            folder.open(readOnly ? Folder.READ_ONLY : Folder.READ_WRITE);
            return folder;
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new MailException(e);
        }
    }

    private MimeMessage build(Mail mail) throws Exception {
        Session session = session();
        Assert.notNull(session, "Parameter \"session\" must not null. ");
        String from = mail.getFrom();
        String to = mail.getTo();
        String replyTo = mail.getReplyTo();
        String cc = mail.getCc();
        String bcc = mail.getBcc();
        Date sentDate = mail.getSentDate();
        String charset = mail.getCharset();
        if (StringUtils.isBlank(charset)) {
            charset = DEFAULT_ENCODING_NAME;
        }
        String subject = mail.getSubject();
        String content = mail.getContent();
        Map<String, Object> attachments = mail.getAttachments();
        if (StringUtils.isBlank(from) && StringUtils.isNotBlank(username)) {
            from = username;
        }
        Assert.notEmpty(from, "Parameter \"from\" must not blank. ");
        Assert.notEmpty(to, "Parameter \"to\" must not blank. ");


        MimeMessage mimeMessage = new MimeMessage(session);
        mimeMessage.setFrom(from);
        mimeMessage.addRecipients(Message.RecipientType.TO, parseAddress(to));
        if (StringUtils.isNotBlank(replyTo)) {
            mimeMessage.setReplyTo(parseAddress(replyTo));
        }
        if (StringUtils.isNotBlank(cc)) {
            mimeMessage.addRecipients(Message.RecipientType.CC, parseAddress(cc));
        }
        if (StringUtils.isNotBlank(bcc)) {
            mimeMessage.addRecipients(Message.RecipientType.BCC, parseAddress(bcc));
        }
        if (sentDate != null) {
            mimeMessage.setSentDate(sentDate);
        }
        if (StringUtils.isNotBlank(subject)) {
            mimeMessage.setSubject(subject, charset);
        }
//        mimeMessage.setText(content, encoding);
        MimeMultipart mimeMultipart = new MimeMultipart();
        if (StringUtils.isNotBlank(content)) {
            String contentType = "text/html; charset=" + charset;
            MimeBodyPart mimeBodyPart = new MimeBodyPart();
            mimeBodyPart.setContent(content, contentType);
            mimeMultipart.addBodyPart(mimeBodyPart);
        }
        if (MapUtils.isNotEmpty(attachments)) {
            for (Map.Entry<String, Object> entry : attachments.entrySet()) {
                String entryKey = entry.getKey();
                if (StringUtils.isBlank(entryKey)) { continue; }
                Object entryVal = entry.getValue();
                if (entryVal == null) { continue; }
                if (entryVal instanceof File) {
                    MimeBodyPart filePart = new MimeBodyPart();
                    File file = cast(entryVal);
                    filePart.setDataHandler(new DataHandler(new FileDataSource(file)));
                    filePart.setFileName(MimeUtility.encodeWord(entryKey, charset, "B"));
                    mimeMultipart.addBodyPart(filePart);
                }
//                else if (entryVal instanceof byte[]) {
//                }
//                else if (entryVal instanceof InputStream) {
//                }
            }
        }
        mimeMessage.setContent(mimeMultipart);
        return mimeMessage;
    }

    public Mail parse(Message message) throws IOException, MessagingException {
        Mail email = new SimpleMail();
        email.rawData(message);
        if (message instanceof MimeMessage) {
            email.setId(((MimeMessage) message).getMessageID());
        }
        String[] xPrioHeaders = message.getHeader(X_PRIORITY);
        email.put("priority", ArrayUtils.isNotEmpty(xPrioHeaders) ? xPrioHeaders[0] : null);
        email.put("size", message.getSize());
        Flags flags = message.getFlags();
        email.put("isRead", flags.contains(SEEN));
        email.setSentDate(message.getSentDate());
        Address[] from = message.getFrom();
        email.setFrom(formatAddress(from));

        Address[] recipients = message.getRecipients(TO);
        email.setTo(formatAddress(recipients));

        Address[] tmp = message.getRecipients(CC);
        if (tmp != null) {
            email.setCc(formatAddress(tmp));
        }
        tmp = message.getRecipients(BCC);
        if (tmp != null) {
            email.setBcc(formatAddress(tmp));
        }
        String subject = message.getSubject();
        subject = MimeUtility.decodeText(subject);
        email.setSubject(subject);
        email.setContent(takeContent(message));
        email.put("hasAttach", containAttach(message));
        return email;
    }

    private String takeContent(Part part) throws IOException, MessagingException {
        StringBuilder builder = new StringBuilder();
        boolean containAttach = part.getContentType().contains(STRING_NAME);
        if (part.isMimeType(TEXT_ALL) && !containAttach) {
            builder.append(part.getContent().toString());
        }
        else if (part.isMimeType(MESSAGE_RFC822)) {
            builder.append(takeContent((Part) part.getContent()));
        }
        else if (part.isMimeType(MULTIPART_ALL)) {
            Multipart multipart = (Multipart) part.getContent();
            int partCount = multipart.getCount();
            for (int i = 0; i < partCount; i++) {
                BodyPart bodyPart = multipart.getBodyPart(i);
                builder.append(takeContent(bodyPart));
            }
        }
        return builder.toString();
    }

    private boolean containAttach(Part part) throws IOException, MessagingException {
        boolean hasAttach = false;
        if (part.isMimeType(MULTIPART_ALL)) {
            // Handle multipart
            MimeMultipart multipart = (MimeMultipart) part.getContent();
            int partCount = multipart.getCount();
            for (int i = 0; i < partCount; i++) {
                BodyPart bPart = multipart.getBodyPart(i);
                String disp = bPart.getDisposition();
                boolean isAttach = disp != null, isInline = isAttach;
                isAttach = isAttach && disp.equalsIgnoreCase(Part.ATTACHMENT);
                isInline = isInline && disp.equalsIgnoreCase(Part.INLINE);
                if (isAttach || isInline ||
                        bPart.isMimeType(APPLICATION_ALL) ||
                        bPart.getContentType().contains(STRING_NAME)) {
                    hasAttach = true;
                }
                else if (bPart.isMimeType(MULTIPART_ALL)) {
                    hasAttach = containAttach(bPart);
                }
                if (hasAttach) { break; }
            }
        }
        else if (part.isMimeType(MESSAGE_RFC822)) {
            hasAttach = containAttach((Part) part.getContent());
        }
        return hasAttach;
    }

    private List<File> saveAttach(Part part, File dir) throws IOException, MessagingException {
        List<File> files = new ArrayList<File>();
        if (part.isMimeType(MULTIPART_ALL)) {
            // Handle multipart
            Multipart multipart = (Multipart) part.getContent();
            int partCount = multipart.getCount();
            for (int i = 0; i < partCount; i++) {
                BodyPart bPart = multipart.getBodyPart(i);
                String disp = bPart.getDisposition();
                boolean isAttach = disp != null, isInline = isAttach;
                boolean hasName = isAttach, isApplication = isAttach;
                isAttach = isAttach && disp.equalsIgnoreCase(Part.ATTACHMENT);
                isInline = isInline && disp.equalsIgnoreCase(Part.INLINE);
                hasName = hasName && bPart.getContentType().contains(STRING_NAME);
                isApplication = isApplication && bPart.isMimeType(APPLICATION_ALL);
                if (isAttach || isInline || hasName || isApplication) {
                    InputStream in = null;
                    OutputStream out = null;
                    try {
                        in = bPart.getInputStream();
                        String fName = bPart.getFileName();
                        fName = MimeUtility.decodeText(fName);
                        File dest = new File(dir, fName);
                        // Prevent dest file already exist.
                        if (!dest.createNewFile()) {
                            throw new IOException("Create file \"" + dest + "\" failure. ");
                        }
                        files.add(dest);
                        out = new FileOutputStream(dest);
                        IOUtils.copyLarge(in, out);
                        out.flush();
                    }
                    finally {
                        CloseUtils.closeQuietly(in);
                        CloseUtils.closeQuietly(out);
                    }
                }
                else if (bPart.isMimeType(MULTIPART_ALL)) {
                    files.addAll(saveAttach(bPart, dir));
                }
            }
        }
        else if (part.isMimeType(MESSAGE_RFC822)) {
            files.addAll(saveAttach((Part) part.getContent(), dir));
        }
        return files;
    }

    public InternetAddress[] parseAddress(String addressList) {
        try {
            return InternetAddress.parse(addressList);
        }
        catch (Exception e) {
            throw ExceptionUtils.wrap(e);
        }
    }

    public String formatAddress(Address[] addresses) {

        return InternetAddress.toUnicodeString(addresses);
    }

//    public String formatAddress() {
//        StringBuilder builder = new StringBuilder();
//        builder.append("MessageId   : ").append(messageId).append(NEWLINE);
//        String fromStr = from.size() > 2 ? Email.serializeAddress(from.subList(0, 2)) + " ..." : Email.serializeAddress(from);
//        builder.append("From        : ").append(fromStr).append(NEWLINE);
//        String toStr = to.size() > 2 ? Email.serializeAddress(to.subList(0, 2)) + " ..." : Email.serializeAddress(to);
//        builder.append("To          : ").append(toStr).append(NEWLINE);
//        if (CollectionUtils.isNotEmpty(cc)) {
//            String ccStr = cc.size() > 2 ? Email.serializeAddress(cc.subList(0, 2)) + " ..." : Email.serializeAddress(cc);
//            builder.append("Cc          : ").append(ccStr).append(NEWLINE);
//        }
//        if (CollectionUtils.isNotEmpty(bcc)) {
//            String bccStr = bcc.size() > 2 ? Email.serializeAddress(bcc.subList(0, 2)) + " ..." : Email.serializeAddress(bcc);
//            builder.append("Bcc         : ").append(bccStr).append(NEWLINE);
//        }
//        builder.append("SentDate    : ").append(DateUtils.create(sentDate).formatAddress()).append(NEWLINE);
//        builder.append("Priority    : ").append(priority).append(NEWLINE);
//        builder.append("Size        : ").append(size).append(" Byte").append(NEWLINE);
//        builder.append("HasAttach   : ").append(hasAttach).append(NEWLINE);
//        builder.append("IsRead      : ").append(isRead).append(NEWLINE);
//        builder.append("IsTextEmail : ").append(isTextEmail).append(NEWLINE);
//        String subject = this.subject.trim();
//        subject = subject.length() > 36 ? subject.substring(0, 36) : subject;
//        builder.append("Subject     : ").append(subject).append(NEWLINE);
//        String content = isTextEmail ? textContent : htmlContent;
//        content = content.trim();
//        content = content.length() > 200 ? content.substring(0, 200) : content;
//        builder.append("Summary     : ").append(content).append(NEWLINE);
//        return builder.formatAddress();
//    }

//    public void saveAttach(File path) throws IOException {
//        if (!path.exists() && !path.mkdirs()) {
//            throw new IOException("Create directory \"" + path + "\" failure. ");
//        }
//        if (hasAttach && MapUtils.isEmpty(this.files)) {
//            List<File> files = Email.saveAttach(message, path);
//            if (CollectionUtils.isEmpty(files)) { return this; }
//            this.addFiles(files);
//        }
//    }


    @Override
    public void send(Mail... mails) {
        try {
            Session session = session();
            Assert.notNull(session, "Parameter \"session\" must not null. ");
            for (Mail mail : mails) {
                if (mail == null) { continue; }
                MimeMessage mimeMessage = build(mail);
                Transport.send(mimeMessage, username, password);
                String id = mimeMessage.getMessageID();
                mail.setId(id);
            }
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new MailException(e);
        }
    }

    @Override
    public Integer getTotalMailCount(String folderName) {
        try {
            Folder folder = getFolder(folderName);
            return folder.getMessageCount();
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new MailException(e);
        }
    }

    @Override
    public Integer getNewMailCount(String folderName) {
        try {
            Folder folder = getFolder(folderName);
            return folder.getNewMessageCount();
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new MailException(e);
        }
    }

    @Override
    public Integer getUnreadMailCount(String folderName) {
        try {
            Folder folder = getFolder(folderName);
            return folder.getUnreadMessageCount();
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new MailException(e);
        }
    }

    @Override
    public Integer getDeletedMailCount(String folderName) {
        try {
            Folder folder = getFolder(folderName);
            return folder.getDeletedMessageCount();
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new MailException(e);
        }
    }

    @Override
    public Mail getMail(String folderName, Integer number) {
        try {
            if (number <= ZERO) {
                throw new IllegalArgumentException();
            }
            Folder folder = getFolder(folderName);
            Message message = folder.getMessage(number);
            return parse(message);
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new MailException(e);
        }
    }

    @Override
    public List<Mail> getMails(String folderName, Integer startNumber, Integer endNumber) {
        try {
            if (startNumber <= ZERO) {
                throw new IllegalArgumentException();
            }
            Folder folder = getFolder(folderName);
            int messageCount = folder.getMessageCount();
            if (endNumber > messageCount) {
                throw new IllegalArgumentException();
            }
            List<Mail> result = new ArrayList<Mail>();
            for (int i = startNumber; i <= endNumber; i++) {
                Message message = folder.getMessage(i);
                Mail mail = parse(message);
                result.add(mail);
            }
            return result;
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new MailException(e);
        }
    }

}

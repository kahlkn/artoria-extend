package artoria.mail;

import artoria.data.ExtraData;
import artoria.data.RawData;

import java.util.Date;
import java.util.Map;

public interface Mail extends ExtraData, RawData {

    String getId();

    void setId(String id);

    String getFrom();

    void setFrom(String from);

    String getReplyTo();

    void setReplyTo(String replyTo);

    String getTo();

    void setTo(String to);

    String getCc();

    void setCc(String cc);

    String getBcc();

    void setBcc(String bcc);

    Date getSentDate();

    void setSentDate(Date sentDate);

    String getCharset();

    void setCharset(String charset);

    String getSubject();

    void setSubject(String subject);

    String getContent();

    void setContent(String content);


    Object getAttachment(String attachmentName);

    void addAttachment(String attachmentName, Object attachmentContent);

    void addAttachments(Map<String, String> attachments);

    boolean containsAttachment(String attachmentName);

    void removeAttachment(String attachmentName);

    Map<String, Object> getAttachments();

    void clearAttachments();


}

package artoria.mail;

import artoria.data.AbstractExtraData;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SimpleMail extends AbstractExtraData implements Mail {
    private Map<String, Object> attachmentMap = new HashMap<String, Object>();
    private Object rawData;
    private String id;
    private String from;
    private String replyTo;
    private String to;
    private String cc;
    private String bcc;
    private Date sentDate;
    private String charset;
    private String subject;
    private String content;

    @Override
    public String getId() {

        return id;
    }

    @Override
    public void setId(String id) {

        this.id = id;
    }

    @Override
    public String getFrom() {

        return from;
    }

    @Override
    public void setFrom(String from) {

        this.from = from;
    }

    @Override
    public String getReplyTo() {

        return replyTo;
    }

    @Override
    public void setReplyTo(String replyTo) {

        this.replyTo = replyTo;
    }

    @Override
    public String getTo() {

        return to;
    }

    @Override
    public void setTo(String to) {

        this.to = to;
    }

    @Override
    public String getCc() {

        return cc;
    }

    @Override
    public void setCc(String cc) {

        this.cc = cc;
    }

    @Override
    public String getBcc() {

        return bcc;
    }

    @Override
    public void setBcc(String bcc) {

        this.bcc = bcc;
    }

    @Override
    public Date getSentDate() {

        return sentDate;
    }

    @Override
    public void setSentDate(Date sentDate) {

        this.sentDate = sentDate;
    }

    @Override
    public String getCharset() {

        return charset;
    }

    @Override
    public void setCharset(String charset) {

        this.charset = charset;
    }

    @Override
    public String getSubject() {

        return subject;
    }

    @Override
    public void setSubject(String subject) {

        this.subject = subject;
    }

    @Override
    public String getContent() {

        return content;
    }

    @Override
    public void setContent(String content) {

        this.content = content;
    }

    @Override
    public Object getAttachment(String attachmentName) {

        return attachmentMap.get(attachmentName);
    }

    @Override
    public void addAttachment(String attachmentName, Object attachmentContent) {

        attachmentMap.put(attachmentName, attachmentContent);
    }

    @Override
    public void addAttachments(Map<String, String> attachments) {

        attachmentMap.putAll(attachments);
    }

    @Override
    public boolean containsAttachment(String attachmentName) {

        return attachmentMap.containsKey(attachmentName);
    }

    @Override
    public void removeAttachment(String attachmentName) {

        attachmentMap.remove(attachmentName);
    }

    @Override
    public Map<String, Object> getAttachments() {

        return Collections.unmodifiableMap(attachmentMap);
    }

    @Override
    public void clearAttachments() {

        attachmentMap.clear();
    }

    @Override
    public Object rawData() {

        return rawData;
    }

    @Override
    public void rawData(Object rawData) {

        this.rawData = rawData;
    }

}

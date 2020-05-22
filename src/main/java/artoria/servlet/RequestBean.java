package artoria.servlet;

import artoria.time.DateUtils;
import artoria.util.MapUtils;
import artoria.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static artoria.common.Constants.*;

/**
 * Request bean.
 * @author Kahle
 */
public class RequestBean {
    private Date receiveTime;
    private String requestURL;
    private String method;
    private String contentType;
    private String remoteAddress;
    private String remotePort;
    private String characterEncoding;
    private Map<String, String> cookies;
    private Map<String, List<String>> headers;
    private Map<String, List<String>> parameters;

    public Date getReceiveTime() {

        return receiveTime;
    }

    public void setReceiveTime(Date receiveTime) {

        this.receiveTime = receiveTime;
    }

    public String getRequestURL() {

        return requestURL;
    }

    public void setRequestURL(String requestURL) {

        this.requestURL = requestURL;
    }

    public String getMethod() {

        return method;
    }

    public void setMethod(String method) {

        this.method = method;
    }

    public String getContentType() {

        return contentType;
    }

    public void setContentType(String contentType) {

        this.contentType = contentType;
    }

    public String getRemoteAddress() {

        return remoteAddress;
    }

    public void setRemoteAddress(String remoteAddress) {

        this.remoteAddress = remoteAddress;
    }

    public String getRemotePort() {

        return remotePort;
    }

    public void setRemotePort(String remotePort) {

        this.remotePort = remotePort;
    }

    public String getCharacterEncoding() {

        return characterEncoding;
    }

    public void setCharacterEncoding(String characterEncoding) {

        this.characterEncoding = characterEncoding;
    }

    public Map<String, String> getCookies() {

        return cookies;
    }

    public void setCookies(Map<String, String> cookies) {

        this.cookies = cookies;
    }

    public Map<String, List<String>> getHeaders() {

        return headers;
    }

    public void setHeaders(Map<String, List<String>> headers) {

        this.headers = headers;
    }

    public Map<String, List<String>> getParameters() {

        return parameters;
    }

    public void setParameters(Map<String, List<String>> parameters) {

        this.parameters = parameters;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("---- General ----").append(NEWLINE);
        builder.append("Request URL: ");
        builder.append(requestURL).append(NEWLINE);
        builder.append("Request Method: ");
        builder.append(method).append(NEWLINE);
        builder.append("Remote Address: ");
        builder.append(remoteAddress).append(COLON);
        builder.append(remotePort).append(NEWLINE);
        if (StringUtils.isNotBlank(characterEncoding)) {
            builder.append("Character Encoding: ");
            builder.append(characterEncoding).append(NEWLINE);
        }
        builder.append("---- Headers ----").append(NEWLINE);
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            List<String> valList = entry.getValue();
            String key = entry.getKey();
            if (StringUtils.isBlank(key)) { continue; }
            StringBuilder keyBuilder = new StringBuilder();
            if (key.contains(MINUS)) {
                String[] split = key.split(MINUS);
                for (String word : split) {
                    keyBuilder.append(StringUtils.capitalize(word));
                    keyBuilder.append(MINUS);
                }
                int length = keyBuilder.length();
                if (length > ZERO) { keyBuilder.deleteCharAt(length - ONE); }
            }
            else { keyBuilder.append(StringUtils.capitalize(key)); }
            StringBuilder valBuilder = new StringBuilder();
            for (String val : valList) { valBuilder.append(val).append(COMMA); }
            int length = valBuilder.length();
            if (length > ZERO) { valBuilder.deleteCharAt(length - ONE); }
            builder.append(keyBuilder).append(": ");
            builder.append(valBuilder).append(NEWLINE);
        }
        if (MapUtils.isNotEmpty(parameters)) {
            builder.append("---- Parameters ----").append(NEWLINE);
            for (Map.Entry<String, List<String>> entry : parameters.entrySet()) {
                List<String> valList = entry.getValue();
                String key = entry.getKey();
                for (String val : valList) {
                    builder.append(key).append(": ");
                    builder.append(val).append(NEWLINE);
                }
            }
        }
        builder.append("---- ").append(DateUtils.format(receiveTime));
        builder.append(" ----").append(NEWLINE);
        return builder.toString();
    }

}

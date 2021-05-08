package artoria.event;

import artoria.message.MessageUtils;
import artoria.servlet.RequestUtils;
import artoria.spring.RequestContextUtils;
import artoria.util.Assert;
import artoria.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

import static artoria.common.Constants.COMPUTER_NAME;
import static artoria.common.Constants.HOST_NAME;

public class HttpEventProvider extends AbstractEventProvider {
    private static Logger log = LoggerFactory.getLogger(HttpEventProvider.class);
    private String serverAppId;
    private String destination;
    private String anonymousIdName;
    private String tokenIdName;
    private String clientAppIdName;

    public HttpEventProvider(String serverAppId, String destination,
                             String anonymousIdName, String tokenIdName, String clientAppIdName) {
        Assert.notBlank(destination, "Parameter \"destination\" must not blank. ");
        this.serverAppId = serverAppId;
        this.destination = destination;
        this.anonymousIdName = anonymousIdName;
        this.tokenIdName = tokenIdName;
        this.clientAppIdName = clientAppIdName;
    }

    @Override
    protected void op(Map<String, Object> properties) {
        if (properties == null) { return; }
        HttpServletRequest request = RequestContextUtils.getRequest();
        properties.put("serverId", StringUtils.isNotBlank(HOST_NAME) ? HOST_NAME : COMPUTER_NAME);
        if (StringUtils.isNotBlank(serverAppId)) {
            properties.put("serverAppId", serverAppId);
        }
        if (request == null) { return; }
        if (StringUtils.isNotBlank(tokenIdName)) {
            properties.put("tokenId", request.getHeader(tokenIdName));
        }
        if (StringUtils.isNotBlank(clientAppIdName)) {
            properties.put("clientAppId", request.getHeader(clientAppIdName));
        }
        properties.put("clientUserAgent", RequestUtils.getUserAgent(request));
        properties.put("clientNetAddress", RequestUtils.getRemoteAddress(request));
        properties.put("interfaceId", request.getRequestURI());
        properties.put("requestMethod", request.getMethod());
        properties.put("requestAddress", String.valueOf(request.getRequestURL()));
        properties.put("requestReferer", RequestUtils.getReferer(request));
        // clientName
        // requestInput
        // responseOutput
        // errorMessage
        // processTime
    }

    @Override
    protected void push(Map<String, Object> eventRecord) {
        String anonymousId = (String) eventRecord.get("anonymousId");
        HttpServletRequest request = RequestContextUtils.getRequest();
        if (request != null) {
            if (StringUtils.isBlank(anonymousId)
                    && StringUtils.isNotBlank(anonymousIdName)) {
                anonymousId = request.getHeader(anonymousIdName);
                eventRecord.put("anonymousId", anonymousId);
            }
        }
        MessageUtils.send(destination, eventRecord);
    }

}

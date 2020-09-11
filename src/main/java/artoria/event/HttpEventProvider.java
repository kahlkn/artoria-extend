package artoria.event;

import artoria.message.MessageUtils;
import artoria.servlet.RequestUtils;
import artoria.spring.RequestContextUtils;
import artoria.util.Assert;
import artoria.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.Map;

import static artoria.common.Constants.COMPUTER_NAME;
import static artoria.common.Constants.HOST_NAME;

public class HttpEventProvider implements EventProvider {
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

    protected Map<String, Object> handleProperties(HttpServletRequest request, Map<String, Object> properties) {
        if (properties == null) { properties = new LinkedHashMap<String, Object>(); }
        properties.put("serverId", StringUtils.isNotBlank(HOST_NAME) ? HOST_NAME : COMPUTER_NAME);
        if (StringUtils.isNotBlank(serverAppId)) {
            properties.put("serverAppId", serverAppId);
        }
        if (request == null) { return properties; }
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
        return properties;
    }

    @Override
    public void addEvent(String eventName, String eventType, String distinctId, String anonymousId, Map<String, Object> properties) {
        try {
            Assert.notBlank(eventName, "Parameter \"eventName\" must not blank. ");
            Long eventTime = (Long) properties.get("eventTime");
            if (eventTime == null) { eventTime = System.currentTimeMillis(); }

            HttpServletRequest request = RequestContextUtils.getRequest();
            if (request != null) {
                if (StringUtils.isBlank(anonymousId)
                        && StringUtils.isNotBlank(anonymousIdName)) {
                    anonymousId = request.getHeader(anonymousIdName);
                }
                properties = handleProperties(request, properties);
            }

            if (StringUtils.isBlank(distinctId) && StringUtils.isBlank(anonymousId)) {
                throw new IllegalArgumentException(
                        "Parameter \"distinctId\" and parameter \"anonymousId\" cannot both be blank. "
                );
            }

            Map<String, Object> eventRecord = new LinkedHashMap<String, Object>();
            eventRecord.put("eventName",   eventName);
            eventRecord.put("eventType",   eventType);
            eventRecord.put("eventTime",   eventTime);
            eventRecord.put("distinctId",  distinctId);
            eventRecord.put("anonymousId", anonymousId);
            eventRecord.put("properties",  properties);

            MessageUtils.send(destination, eventRecord);
        }
        catch (Exception e) {
            log.error(getClass().getSimpleName() + ": An error has occurred. ", e);
        }
    }

}

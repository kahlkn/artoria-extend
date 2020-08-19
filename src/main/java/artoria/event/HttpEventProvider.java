package artoria.event;

import artoria.message.Message;
import artoria.message.MessageUtils;
import artoria.message.SimpleMessage;
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
    private String subdivision;
    private String destination;
    private String serverAppId;
    private String anonymousIdName;
    private String tokenIdName;
    private String clientAppIdName;

    public HttpEventProvider(String subdivision, String destination, String serverAppId,
                             String anonymousIdName, String tokenIdName, String clientAppIdName) {
        if (StringUtils.isBlank(subdivision) && StringUtils.isBlank(destination)) {
            throw new IllegalArgumentException(
                    "Parameter \"subdivision\" and parameter \"destination\" cannot both be blank. "
            );
        }
        this.subdivision = subdivision;
        this.destination = destination;
        this.serverAppId = serverAppId;
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
    public void addEvent(String event, String type, Long time, String userId, String anonymousId, Map<String, Object> properties) {
        try {
            Assert.notBlank(event, "Parameter \"event\" must not blank. ");
            if (time == null) { time = System.currentTimeMillis(); }

            HttpServletRequest request = RequestContextUtils.getRequest();
            if (request != null) {
                if (StringUtils.isBlank(anonymousId)
                        && StringUtils.isNotBlank(anonymousIdName)) {
                    anonymousId = request.getHeader(anonymousIdName);
                }
                properties = handleProperties(request, properties);
            }

            if (StringUtils.isBlank(userId) && StringUtils.isBlank(anonymousId)) {
                throw new IllegalArgumentException(
                        "Parameter \"userId\" and parameter \"anonymousId\" cannot both be blank. "
                );
            }

            Map<String, Object> eventRecord = new LinkedHashMap<String, Object>();
            eventRecord.put("properties", properties);
            eventRecord.put("event",  event);
            eventRecord.put("type",   type);
            eventRecord.put("time",   time);
            eventRecord.put("userId", userId);
            eventRecord.put("anonymousId", anonymousId);

            Message message = new SimpleMessage();
            message.setDestination(destination);
            message.setBody(eventRecord);
            MessageUtils.send(message);
        }
        catch (Exception e) {
            log.error(getClass().getSimpleName() + ": An error has occurred. ", e);
        }
    }

}

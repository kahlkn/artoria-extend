package artoria.message;

import artoria.beans.BeanMap;
import artoria.beans.BeanUtils;
import artoria.common.AsyncCallback;
import artoria.exchange.JsonUtils;
import artoria.lifecycle.LifecycleException;
import artoria.util.MapUtils;
import artoria.util.ObjectUtils;
import artoria.util.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.MessageProperties;

import java.util.Map;
import java.util.Set;

import static artoria.util.ObjectUtils.cast;

public class AmqpMessageProvider implements MessageProvider {
    private AmqpTemplate amqpTemplate;

    public AmqpMessageProvider(AmqpTemplate amqpTemplate) {

        this.amqpTemplate = amqpTemplate;
    }

    @Override
    public void initialize() throws LifecycleException {

    }

    @Override
    public void listening(String subdivision, String destination, MessageListener listener) throws MessageException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void removeListening(String subdivision, String destination, MessageListener listener) throws MessageException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void send(Message message) throws MessageException {
        String subdivision = message.getSubdivision();
        String destination = message.getDestination();
        Map<String, Object> properties = message.getProperties();
        Object body = message.getBody();
        byte[] bodyBytes;
        if (body instanceof byte[]) {
            bodyBytes = (byte[]) body;
        }
        else if (body instanceof String) {
            bodyBytes = ((String) body).getBytes();
        }
        else {
            String jsonString = JsonUtils.toJsonString(body);
            bodyBytes = jsonString.getBytes();
        }
        MessageProperties messageProperties = new MessageProperties();
        if (MapUtils.isNotEmpty(properties)) {
            Object headers = properties.get("headers");
            if (headers instanceof Map && !ObjectUtils.isEmpty(headers)) {
                Map<String, Object> map = cast(headers);
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    Object value = entry.getValue();
                    String key = entry.getKey();
                    messageProperties.setHeader(key, value);
                }
                properties.remove("headers");
            }
            BeanMap beanMap = BeanUtils.createBeanMap(messageProperties);
            Set<Object> keySet = beanMap.keySet();
            for (Object key : keySet) {
                String castKey = ObjectUtils.cast(key);
                Object val = properties.get(castKey);
                if (val == null) { continue; }
                beanMap.put(key, val);
            }
        }
        org.springframework.amqp.core.Message input =
                new org.springframework.amqp.core.Message(bodyBytes, messageProperties);
        if (StringUtils.isNotBlank(destination)) {
            amqpTemplate.send(subdivision, destination, input);
        }
        else {
            amqpTemplate.send(destination, input);
        }
    }

    @Override
    public void sendAsync(Message message, AsyncCallback<Object> callback) throws MessageException {

        throw new UnsupportedOperationException();
    }

    @Override
    public Message receive(String subdivision, String destination) throws MessageException {
        org.springframework.amqp.core.Message message = amqpTemplate.receive(destination);
        Message result = new SimpleMessage();
        result.setSubdivision(subdivision);
        result.setDestination(destination);
        byte[] body = message.getBody();
        MessageProperties messageProperties = message.getMessageProperties();
        result.setBody(body);
        Map<String, Object> beanToMap = BeanUtils.beanToMap(messageProperties);
        if (MapUtils.isNotEmpty(beanToMap)) { result.addProperties(beanToMap); }
        return result;
    }

    @Override
    public Message receive(String subdivision, String destination, long timeoutMillis) throws MessageException {
        org.springframework.amqp.core.Message message = amqpTemplate.receive(destination, timeoutMillis);
        Message result = new SimpleMessage();
        result.setSubdivision(subdivision);
        result.setDestination(destination);
        byte[] body = message.getBody();
        MessageProperties messageProperties = message.getMessageProperties();
        result.setBody(body);
        Map<String, Object> beanToMap = BeanUtils.beanToMap(messageProperties);
        if (MapUtils.isNotEmpty(beanToMap)) { result.addProperties(beanToMap); }
        return result;
    }

    @Override
    public void destroy() throws Exception {

    }

}

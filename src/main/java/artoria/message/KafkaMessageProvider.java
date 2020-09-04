package artoria.message;

import artoria.common.AsyncCallback;
import artoria.lifecycle.LifecycleException;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.kafka.support.converter.RecordMessageConverter;
import org.springframework.lang.NonNull;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.LinkedHashMap;
import java.util.Map;

import static artoria.util.ObjectUtils.cast;

public class KafkaMessageProvider implements MessageProvider {
    private KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaMessageProvider(KafkaTemplate<String, Object> kafkaTemplate) {

        this.kafkaTemplate = kafkaTemplate;
    }

    private class ListenableFutureCallbackAdapter implements ListenableFutureCallback<SendResult<String, Object>> {
        private AsyncCallback<Object> callback;

        ListenableFutureCallbackAdapter(AsyncCallback<Object> callback) {

            this.callback = callback;
        }

        @Override
        public void onSuccess(SendResult<String, Object> result) {

            callback.onSuccess(result);
        }

        @Override
        public void onFailure(@NonNull Throwable ex) {

            callback.onFailure(ex);
        }

    }

    private ListenableFuture<SendResult<String, Object>> doSend(Message message) {
        Map<String, Object> properties = message.getProperties();
        String destination = message.getDestination();
        String messageId = message.getMessageId();
        Object body = message.getBody();
        Map<String, Object> headers = new LinkedHashMap<String, Object>();
        headers.put("messageId", messageId);
        headers.put("destination", destination);
        headers.putAll(properties);
//        if (StringUtils.isNotBlank(destination)) {
//            headers.put(KafkaHeaders.MESSAGE_KEY, destination);
//        }
        org.springframework.messaging.Message<Object> input = new GenericMessage<Object>(body, headers);
        RecordMessageConverter messageConverter = (RecordMessageConverter) kafkaTemplate.getMessageConverter();
        ProducerRecord<String, Object> producerRecord = cast(messageConverter.fromMessage(input, destination));
        return kafkaTemplate.send(producerRecord);
    }

    @Override
    public void initialize() throws LifecycleException {

    }

    @Override
    public void listening(String destination, Map<String, Object> parameters, MessageListener listener) throws MessageException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void removeListening(String destination, Map<String, Object> parameters, MessageListener listener) throws MessageException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void send(Message message) throws MessageException {
        try {
            ListenableFuture<SendResult<String, Object>> future = doSend(message);
            future.get();
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new MessageException(e);
        }
    }

    @Override
    public void sendAsync(Message message, AsyncCallback<Object> callback) throws MessageException {
        try {
            ListenableFuture<SendResult<String, Object>> future = doSend(message);
            future.addCallback(new ListenableFutureCallbackAdapter(callback));
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new MessageException(e);
        }
    }

    @Override
    public Message receive(String destination, Map<String, Object> parameters) throws MessageException {

        throw new UnsupportedOperationException();
    }

    @Override
    public Message receive(String destination, Map<String, Object> parameters, long timeoutMillis) throws MessageException {

        throw new UnsupportedOperationException();
    }

    @Override
    public void destroy() throws Exception {

    }

}

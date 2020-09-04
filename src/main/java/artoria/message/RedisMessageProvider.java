package artoria.message;

import artoria.common.AsyncCallback;
import artoria.exchange.JsonUtils;
import artoria.identifier.IdentifierUtils;
import artoria.lifecycle.LifecycleException;
import artoria.logging.Logger;
import artoria.logging.LoggerFactory;
import artoria.thread.SimpleThreadFactory;
import artoria.util.*;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import static artoria.common.Constants.*;
import static java.lang.Boolean.TRUE;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class RedisMessageProvider implements MessageProvider {
    private static final String MQ_LISTENING_QUEUE = "MQ:LISTENING_QUEUE:";
    private static final String MQ_LISTENING_TOPIC = "MQ:LISTENING_TOPIC:";
    private static final String MQ_MESSAGE_QUEUE = "MQ:MESSAGE_QUEUE:";
    private static final String MQ_MESSAGE_TOPIC = "MQ:MESSAGE_TOPIC:";
    private static final String MQ_CONFIGURATION = "MQ:CONFIGURATION";
    private static final String QUEUE = "QUEUE";
    private static final String TOPIC = "TOPIC";
    private static final String LISTENING_ID = (StringUtils.isNotBlank(HOST_NAME) ? HOST_NAME : COMPUTER_NAME)
            + MINUS + IdentifierUtils.nextStringIdentifier().toUpperCase();
    private static final String THREAD_NAME_PREFIX = "redis-message-provider-executor";
    private static final long DEFAULT_TIMEOUT = 10000L;
    private static final long DEFAULT_PERIOD = 5000L;
    private static Logger log = LoggerFactory.getLogger(RedisMessageProvider.class);
    private final Map<String, LinkedList<MessageListener>> listenerMap = new ConcurrentHashMap<String, LinkedList<MessageListener>>();
    private final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;
    private final StringRedisTemplate stringRedisTemplate;

    public RedisMessageProvider(StringRedisTemplate stringRedisTemplate) {

        this(stringRedisTemplate, DEFAULT_PERIOD);
    }

    public RedisMessageProvider(StringRedisTemplate stringRedisTemplate, long period) {
        this(
                stringRedisTemplate,
                new ScheduledThreadPoolExecutor(
                        FOUR,
                        new SimpleThreadFactory(THREAD_NAME_PREFIX, TRUE)
                ),
                period
        );
    }

    public RedisMessageProvider(StringRedisTemplate stringRedisTemplate, ScheduledThreadPoolExecutor scheduledThreadPoolExecutor, long period) {
        Assert.notNull(scheduledThreadPoolExecutor, "Parameter \"scheduledThreadPoolExecutor\" must not null. ");
        Assert.notNull(stringRedisTemplate, "Parameter \"stringRedisTemplate\" must not null. ");
        Assert.isTrue(period > ZERO, "Parameter \"period\" must greater than 0. ");
        this.scheduledThreadPoolExecutor = scheduledThreadPoolExecutor;
        this.stringRedisTemplate = stringRedisTemplate;
        // Schedule tasks for the thread pool.
        RedisMessageDispatcher messageDispatcher = new RedisMessageDispatcher();
        scheduledThreadPoolExecutor.scheduleAtFixedRate(messageDispatcher, period, period, MILLISECONDS);
        ShutdownHookUtils.addExecutorService(scheduledThreadPoolExecutor);
    }

    private class RedisMessageDispatcher implements Runnable {
        private static final long LISTENING_EXPIRE_TIME = 10000L;

        private void consumeMessage(MessageListener listener, Message message) {
            try {
                listener.onMessage(message);
            }
            catch (Exception e) {
                log.info("Consume message error. ", e);
            }
        }

        private void listening(String destination, String messageDestinationKey) {
            ValueOperations<String, String> opsForValue = stringRedisTemplate.opsForValue();
            if (messageDestinationKey.startsWith(MQ_MESSAGE_QUEUE)) {
                String aliveKey = MQ_LISTENING_QUEUE + destination + COLON + LISTENING_ID;
                opsForValue.set(aliveKey, LISTENING_ID, LISTENING_EXPIRE_TIME, MILLISECONDS);
            }
            else {
                String aliveKey = MQ_LISTENING_TOPIC + destination + COLON + LISTENING_ID;
                opsForValue.set(aliveKey, LISTENING_ID, LISTENING_EXPIRE_TIME, MILLISECONDS);
            }
        }

        @Override
        public void run() {
            try {
                if (MapUtils.isEmpty(listenerMap)) { return; }
                ListOperations<String, String> opsForList = stringRedisTemplate.opsForList();
                for (Map.Entry<String, LinkedList<MessageListener>> entry : listenerMap.entrySet()) {
                    LinkedList<MessageListener> listenerList = entry.getValue();
                    String destination = entry.getKey();
                    if (CollectionUtils.isEmpty(listenerList)) { continue; }
                    if (StringUtils.isBlank(destination)) { continue; }
                    String messageDestinationKey = messageDestinationKey(destination, true);
                    listening(destination, messageDestinationKey);
                    String leftPop;
                    while (StringUtils.isNotBlank(leftPop = opsForList.leftPop(messageDestinationKey))) {
                        Message message = JsonUtils.parseObject(leftPop, SimpleMessage.class);
                        if (messageDestinationKey.startsWith(MQ_MESSAGE_TOPIC)) {
                            for (MessageListener messageListener : listenerList) {
                                consumeMessage(messageListener, message);
                            }
                        }
                        else {
                            MessageListener messageListener = listenerList.removeFirst();
                            consumeMessage(messageListener, message);
                            listenerList.addLast(messageListener);
                        }
                    }
                }
            }
            catch (Exception e) {
                log.error("Redis message dispatcher run error", e);
            }
        }

    }

    private String messageDestinationKey(String destination, boolean withOwn) {
        Assert.notBlank(destination, "Parameter \"destination\" must not blank. ");
        HashOperations<String, Object, Object> opsForHash = stringRedisTemplate.opsForHash();
        destination = destination.toUpperCase();
        String type = (String) opsForHash.get(MQ_CONFIGURATION, destination);
        if (QUEUE.equals(type)) {
            return MQ_MESSAGE_QUEUE + destination;
        }
        else if (TOPIC.equals(type)) {
            String messageDestinationKey = MQ_MESSAGE_TOPIC + destination;
            if (withOwn) {
                messageDestinationKey += COLON + LISTENING_ID;
            }
            return messageDestinationKey;
        }
        else {
            throw new MessageException("Destination \"" + destination + "\" must exist. ");
        }
    }

    public void createQueue(String destination) {
        Assert.notBlank(destination, "Parameter \"destination\" must not blank. ");
        HashOperations<String, Object, Object> opsForHash = stringRedisTemplate.opsForHash();
        destination = destination.toUpperCase();
        opsForHash.put(MQ_CONFIGURATION, destination, QUEUE);
    }

    public void createTopic(String destination) {
        Assert.notBlank(destination, "Parameter \"destination\" must not blank. ");
        HashOperations<String, Object, Object> opsForHash = stringRedisTemplate.opsForHash();
        destination = destination.toUpperCase();
        opsForHash.put(MQ_CONFIGURATION, destination, TOPIC);
    }

    @Override
    public void initialize() throws LifecycleException {

    }

    @Override
    public void listening(String destination, Map<String, Object> parameters, MessageListener listener) throws MessageException {
        Assert.notBlank(destination, "Parameter \"destination\" must not blank. ");
        Assert.notNull(listener, "Parameter \"listener\" must not null. ");
        destination = destination.toUpperCase();
        LinkedList<MessageListener> listenerList = listenerMap.get(destination);
        if (listenerList == null) {
            listenerList = new LinkedList<MessageListener>();
            listenerMap.put(destination, listenerList);
        }
        listenerList.add(listener);
    }

    @Override
    public void removeListening(String destination, Map<String, Object> parameters, MessageListener listener) throws MessageException {
        Assert.notBlank(destination, "Parameter \"destination\" must not blank. ");
        Assert.notNull(listener, "Parameter \"listener\" must not null. ");
        destination = destination.toUpperCase();
        List<MessageListener> listenerList = listenerMap.get(destination);
        if (listenerList == null) { return; }
        listenerList.remove(listener);
    }

    @Override
    public void send(Message message) throws MessageException {
        Assert.notNull(message, "Parameter \"message\" must not null. ");
        String destination = message.getDestination();
        Assert.notBlank(destination, "Parameter \"destination\" must not blank. ");
        String messageId = message.getMessageId();
        if (StringUtils.isBlank(messageId)) {
            messageId = IdentifierUtils.nextStringIdentifier();
            message.setMessageId(messageId);
        }
        destination = destination.toUpperCase();
        String messageDestinationKey = messageDestinationKey(destination, false);
        String jsonString = JsonUtils.toJsonString(message);
        ListOperations<String, String> opsForList = stringRedisTemplate.opsForList();
        if (messageDestinationKey.startsWith(MQ_MESSAGE_TOPIC)) {
            String pattern = MQ_LISTENING_TOPIC + destination + COLON + ASTERISK;
            Set<String> keys = stringRedisTemplate.keys(pattern);
            if (CollectionUtils.isEmpty(keys)) { return; }
            for (String key : keys) {
                if (StringUtils.isBlank(key)) { continue; }
                int beginIndex = key.lastIndexOf(COLON) + ONE;
                String listeningId = key.substring(beginIndex, key.length());
                if (StringUtils.isBlank(listeningId)) { continue; }
                String newKey = messageDestinationKey + COLON + listeningId;
                opsForList.rightPush(newKey, jsonString);
            }
        }
        else {
            opsForList.rightPush(messageDestinationKey, jsonString);
        }
    }

    @Override
    public void sendAsync(Message message, AsyncCallback<Object> callback) throws MessageException {
        Assert.notNull(message, "Parameter \"message\" must not null. ");
        Assert.notNull(callback, "Parameter \"callback\" must not null. ");
        String destination = message.getDestination();
        Assert.notBlank(destination, "Parameter \"destination\" must not blank. ");
        Runnable runnable = new SimpleAsyncSender(this, message, callback);
        scheduledThreadPoolExecutor.execute(runnable);
    }

    @Override
    public Message receive(String destination, Map<String, Object> parameters) throws MessageException {
        Assert.notBlank(destination, "Parameter \"destination\" must not blank. ");
        destination = destination.toUpperCase();
        String messageDestinationKey = messageDestinationKey(destination, true);
        ListOperations<String, String> opsForList = stringRedisTemplate.opsForList();
        String leftPop = opsForList.leftPop(messageDestinationKey);
        return JsonUtils.parseObject(leftPop, SimpleMessage.class);
    }

    @Override
    public Message receive(String destination, Map<String, Object> parameters, long timeoutMillis) throws MessageException {
        Assert.notBlank(destination, "Parameter \"destination\" must not blank. ");
        destination = destination.toUpperCase();
        String messageDestinationKey = messageDestinationKey(destination, true);
        ListOperations<String, String> opsForList = stringRedisTemplate.opsForList();
        String leftPop = opsForList.leftPop(messageDestinationKey, DEFAULT_TIMEOUT, MILLISECONDS);
        return JsonUtils.parseObject(leftPop, SimpleMessage.class);
    }

    @Override
    public void destroy() throws Exception {
        if (scheduledThreadPoolExecutor != null
                && !scheduledThreadPoolExecutor.isShutdown()) {
            scheduledThreadPoolExecutor.shutdown();
            ShutdownHookUtils.removeExecutorService(scheduledThreadPoolExecutor);
        }
    }

}

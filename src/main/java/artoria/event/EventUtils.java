package artoria.event;

import artoria.collection.ReferenceMap;
import artoria.common.Constants;
import artoria.lang.ReferenceType;
import artoria.util.Assert;
import artoria.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

public class EventUtils {
    private static ThreadLocal<Map<String, Event>> threadLocal = new ThreadLocal<Map<String, Event>>();
    private static Logger log = LoggerFactory.getLogger(EventUtils.class);
    private static EventProvider eventProvider;

    public static EventProvider getEventProvider() {
        if (eventProvider != null) { return eventProvider; }
        synchronized (EventUtils.class) {
            if (eventProvider != null) { return eventProvider; }
            EventUtils.setEventProvider(new SimpleEventProvider());
            return eventProvider;
        }
    }

    public static void setEventProvider(EventProvider eventProvider) {
        Assert.notNull(eventProvider, "Parameter \"eventProvider\" must not null. ");
        log.info("Set event provider: {}", eventProvider.getClass().getName());
        EventUtils.eventProvider = eventProvider;
    }

    public static Event record() {

        return record(Constants.DEFAULT);
    }

    public static Event record(String record) {

        Map<String, Event> eventMap = threadLocal.get();
        if (eventMap == null) {
            eventMap = new ReferenceMap<String, Event>(ReferenceType.SOFT);
            threadLocal.set(eventMap);
        }

        Event event = eventMap.get(record);
        if (event == null) {
            event = new Event(record);
            eventMap.put(record, event);
        }

        return event;
    }

    public static void submit() {

        submit(Constants.DEFAULT);
    }

    public static void submit(String record) {

        Map<String, Event> eventMap = threadLocal.get();
        Assert.notNull(eventMap, "The thread local container is not ready. ");

        Event event = eventMap.get(record);
        Assert.notNull(event, "The record for parameter \"record\" does not exist. ");

        Assert.notBlank(event.getName(), "Parameter \"name\" must not blank. ");
        if (StringUtils.isBlank(event.getDistinctId()) && StringUtils.isBlank(event.getAnonymousId())) {
            throw new IllegalArgumentException(
                    "Parameter \"distinctId\" and parameter \"anonymousId\" cannot both be blank. "
            );
        }

        getEventProvider().submit(event.getName(),
                event.getType(),
                event.getTime(),
                event.getDistinctId(),
                event.getAnonymousId(),
                event.getProperties());

        eventMap.remove(record);
    }

    public static void cancel() {

        cancel(Constants.DEFAULT);
    }

    public static void cancel(String record) {
        Map<String, Event> eventMap = threadLocal.get();
        Assert.notNull(eventMap, "The thread local container is not ready. ");
        eventMap.remove(record);
    }

    public static void clear() {

        Map<String, Event> eventMap = threadLocal.get();
        if (eventMap != null) { eventMap.clear(); }

        threadLocal.remove();
    }

    public static class Event {
        private Map<String, Object> properties = new LinkedHashMap<String, Object>();
        private String anonymousId;
        private String distinctId;
        private Long   time;
        private String type;
        private String name;
        private String record;

        Event(String record) {
            this.record = record;
        }

        String getRecord() {

            return record;
        }

        public String getName() {

            return name;
        }

        public Event setName(String name) {
            this.name = name;
            return this;
        }

        public String getType() {

            return type;
        }

        public Event setType(String type) {
            this.type = type;
            return this;
        }

        public Long getTime() {

            return time;
        }

        public Event setTime(Long time) {
            this.time = time;
            return this;
        }

        public String getDistinctId() {

            return distinctId;
        }

        public Event setDistinctId(String distinctId) {
            this.distinctId = distinctId;
            return this;
        }

        public String getAnonymousId() {

            return anonymousId;
        }

        public Event setAnonymousId(String anonymousId) {
            this.anonymousId = anonymousId;
            return this;
        }

        public Map<String, Object> getProperties() {

            return properties;
        }

        public Event addProperties(Map<String, Object> properties) {
            this.properties = properties;
            return this;
        }

        public Event setProperty(String attrName, Object attrValue) {
            properties.put(attrName, attrValue);
            return this;
        }

        public Event removeProperty(String attrName) {
            properties.remove(attrName);
            return this;
        }

        public Object getProperty(String attrName) {

            return properties.get(attrName);
        }

        public void submit() {

            EventUtils.submit(getRecord());
        }

        public void cancel() {

            EventUtils.cancel(getRecord());
        }

    }

}

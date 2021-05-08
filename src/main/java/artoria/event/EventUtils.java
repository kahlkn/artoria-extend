package artoria.event;

import artoria.collection.ReferenceMap;
import artoria.common.Constants;
import artoria.lang.ReferenceType;
import artoria.util.Assert;
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

    public static void submit(String name, String type, String distinctId, Map<String, Object> properties) {

        getEventProvider().submit(name, type, distinctId, properties);
    }

    public static void submit(String name, String type, String distinctId, String anonymousId, Map<String, Object> properties) {

        getEventProvider().submit(name, type, distinctId, anonymousId, properties);
    }

    public static void submit(String name, String type, Long time, String distinctId, String anonymousId, Map<String, Object> properties) {

        getEventProvider().submit(name, type, distinctId, anonymousId, properties);
    }


    public static Event record() {

        return record(Constants.DEFAULT);
    }

    public static Event record(String recordName) {

        Map<String, Event> recordMap = threadLocal.get();
        if (recordMap == null) {
            recordMap = new ReferenceMap<String, Event>(ReferenceType.SOFT);
            threadLocal.set(recordMap);
        }

        Event eventRecord = recordMap.get(recordName);
        if (eventRecord == null) {
            eventRecord = new Event(recordName);
            recordMap.put(recordName, eventRecord);
        }

        return eventRecord;
    }

    public static void submit() {

        submit(Constants.DEFAULT);
    }

    public static void submit(String recordName) {

        Map<String, Event> recordMap = threadLocal.get();

        if (recordMap == null) { return; }

        Event eventRecord = recordMap.get(recordName);

        if (eventRecord == null) { return; }

        submit(eventRecord.getName(),
                eventRecord.getType(),
                eventRecord.getTime(),
                eventRecord.getDistinctId(),
                eventRecord.getAnonymousId(),
                eventRecord.getProperties());

        recordMap.remove(recordName);
    }

    public static void cancel() {

        cancel(Constants.DEFAULT);
    }

    public static void cancel(String recordName) {
        Map<String, Event> recordMap = threadLocal.get();
        if (recordMap == null) { return; }
        recordMap.remove(recordName);
    }

    public static void clear() {

        Map<String, Event> recordMap = threadLocal.get();
        if (recordMap != null) { recordMap.clear(); }

        threadLocal.remove();
    }

    public static class Event {
        private Map<String, Object> properties = new LinkedHashMap<String, Object>();
        private String anonymousId;
        private String distinctId;
        private Long   time;
        private String type;
        private String name;
        private String recordName;

        public Event(String recordName) {
            this.recordName = recordName;
        }

        public String getRecordName() {

            return recordName;
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

            EventUtils.submit(getRecordName());
        }

        public void cancel() {

            EventUtils.cancel(getRecordName());
        }

    }

}

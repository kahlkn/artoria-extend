package artoria.event;

import artoria.logging.Logger;
import artoria.logging.LoggerFactory;
import artoria.time.DateUtils;
import artoria.util.Assert;
import artoria.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;

import static artoria.common.Constants.NEWLINE;
import static artoria.util.ObjectUtils.cast;

public class SimpleEventProvider implements EventProvider {
    private static Logger log = LoggerFactory.getLogger(SimpleEventProvider.class);

    protected void show(Map<String, Object> eventRecord) {
        Map<String, Object> properties = cast(eventRecord.get("properties"));
        String anonymousId = (String) eventRecord.get("anonymousId");
        String distinctId =  (String) eventRecord.get("distinctId");
        Long   time =        (Long) eventRecord.get("time");
        String type =        (String) eventRecord.get("type");
        String name =        (String) eventRecord.get("name");
        String content = NEWLINE +
                "---- Begin Event ----" + NEWLINE +
                "Name:           " + name + NEWLINE +
                "Type:           " + type + NEWLINE +
                "Time:           " + DateUtils.format(time) + NEWLINE +
                "Distinct Id:    " + distinctId + NEWLINE +
                "Anonymous Id:   " + anonymousId + NEWLINE +
                "Properties:     " + properties + NEWLINE +
                "Provider:       " + getClass().getSimpleName() + NEWLINE +
                "---- End Event ----" + NEWLINE;
        log.info(content);
    }

    protected void edit(Map<String, Object> eventRecord) {
    }

    protected void push(Map<String, Object> eventRecord) {

        show(eventRecord);
    }

    @Override
    public void submit(String name, String type, String distinctId, Map<String, Object> properties) {
        Assert.notBlank(distinctId, "Parameter \"distinctId\" must not blank. ");
        submit(name, type, null, distinctId, null, properties);
    }

    @Override
    public void submit(String name, String type, String distinctId, String anonymousId, Map<String, Object> properties) {

        submit(name, type, null, distinctId, anonymousId, properties);
    }

    @Override
    public void submit(String name, String type, Long time, String distinctId, String anonymousId, Map<String, Object> properties) {
        try {
            // Verify parameters.
            Assert.notBlank(name, "Parameter \"name\" must not blank. ");
            if (StringUtils.isBlank(distinctId) && StringUtils.isBlank(anonymousId)) {
                throw new IllegalArgumentException(
                        "Parameter \"distinctId\" and parameter \"anonymousId\" cannot both be blank. "
                );
            }
            if (properties == null) { properties = new LinkedHashMap<String, Object>(); }
            if (time == null) { time = System.currentTimeMillis(); }
            // Build the map.
            Map<String, Object> eventRecord = new LinkedHashMap<String, Object>();
            eventRecord.put("name",        name);
            eventRecord.put("type",        type);
            eventRecord.put("time",        time);
            eventRecord.put("distinctId",  distinctId);
            eventRecord.put("anonymousId", anonymousId);
            eventRecord.put("properties",  properties);
            // Edit the event.
            edit(eventRecord);
            // Push the event.
            push(eventRecord);
        }
        catch (Exception e) {
            log.error(getClass().getSimpleName() + ": An error has occurred. ", e);
        }
    }

}

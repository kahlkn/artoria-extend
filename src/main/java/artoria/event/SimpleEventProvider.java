package artoria.event;

import artoria.logging.Logger;
import artoria.logging.LoggerFactory;
import artoria.time.DateUtils;
import artoria.util.Assert;
import artoria.util.CollectionUtils;
import artoria.util.StringUtils;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static artoria.common.Constants.*;
import static artoria.util.ObjectUtils.cast;
import static java.util.Collections.emptyList;

public class SimpleEventProvider implements EventProvider {
    private static Logger log = LoggerFactory.getLogger(SimpleEventProvider.class);
    private List<String> showPropertyNames;

    public SimpleEventProvider() {

        this(Collections.<String>emptyList());
    }

    public SimpleEventProvider(List<String> showKeys) {

        setShowPropertyNames(showKeys);
    }

    public List<String> getShowPropertyNames() {

        return showPropertyNames;
    }

    public void setShowPropertyNames(List<String> showPropertyNames) {
        if (showPropertyNames == null) { showPropertyNames = emptyList(); }
        this.showPropertyNames = showPropertyNames;
    }

    protected void show(Map<String, Object> eventRecord) {
        Map<String, Object> properties = cast(eventRecord.get("properties"));
        String anonymousId = (String) eventRecord.get("anonymousId");
        String distinctId =  (String) eventRecord.get("distinctId");
        Long   time =        (Long) eventRecord.get("time");
        String type =        (String) eventRecord.get("type");
        String name =        (String) eventRecord.get("name");
        //
        StringBuilder builder = new StringBuilder();
        if (CollectionUtils.isNotEmpty(showPropertyNames)) {
            for (String propertyName : showPropertyNames) {
            if (StringUtils.isBlank(propertyName)) { continue; }
            Object value = properties.get(propertyName);
            if (value == null) { continue; }
            propertyName = StringUtils.capitalize(propertyName);
            builder.append(propertyName).append(COLON);
            int length = SIXTEEN - propertyName.length() - ONE;
            if (length <= ZERO) { length = ONE; }
                for (int i = ZERO; i < length; i++) {
                    builder.append(BLANK_SPACE);
                }
                builder.append(value).append(NEWLINE);
            }
        }
        //
        String content = NEWLINE +
                "---- Begin Event ----" + NEWLINE +
                "Name:           " + name + NEWLINE +
                "Type:           " + type + NEWLINE +
                "Time:           " + DateUtils.format(time) + NEWLINE +
                "DistinctId:     " + distinctId + NEWLINE +
                "AnonymousId:    " + anonymousId + NEWLINE +
                "Provider:       " + getClass().getName() + NEWLINE +
                builder +
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

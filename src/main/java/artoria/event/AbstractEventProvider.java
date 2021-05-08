package artoria.event;

import artoria.util.Assert;
import artoria.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class AbstractEventProvider implements EventProvider {
    private static Logger log = LoggerFactory.getLogger(AbstractEventProvider.class);

    protected void op(Map<String, Object> properties) {
    }

    protected void push(Map<String, Object> eventRecord) {
    }

    protected void log(String format, Object... arguments) {
        log.info(format, arguments);
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
            Assert.notBlank(name, "Parameter \"name\" must not blank. ");
            if (StringUtils.isBlank(distinctId) && StringUtils.isBlank(anonymousId)) {
                throw new IllegalArgumentException(
                        "Parameter \"distinctId\" and parameter \"anonymousId\" cannot both be blank. "
                );
            }
            if (properties == null) { properties = new LinkedHashMap<String, Object>(); }
            if (time == null) { time = System.currentTimeMillis(); }

            op(properties);

            Map<String, Object> eventRecord = new LinkedHashMap<String, Object>();
            eventRecord.put("name",        name);
            eventRecord.put("type",        type);
            eventRecord.put("time",        time);
            eventRecord.put("distinctId",  distinctId);
            eventRecord.put("anonymousId", anonymousId);
            eventRecord.put("properties",  properties);

            push(eventRecord);
        }
        catch (Exception e) {
            log.error(getClass().getSimpleName() + ": An error has occurred. ", e);
        }
    }

}

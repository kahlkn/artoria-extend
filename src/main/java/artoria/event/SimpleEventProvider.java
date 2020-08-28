package artoria.event;

import artoria.logging.Logger;
import artoria.logging.LoggerFactory;
import artoria.time.DateUtils;
import artoria.util.Assert;
import artoria.util.StringUtils;

import java.util.Map;

public class SimpleEventProvider implements EventProvider {
    private static Logger log = LoggerFactory.getLogger(SimpleEventProvider.class);

    @Override
    public void addEvent(String eventName, String eventType, String distinctId, String anonymousId, Map<String, Object> properties) {
        try {
            Assert.notBlank(eventName, "Parameter \"eventName\" must not blank. ");
            if (StringUtils.isBlank(distinctId) && StringUtils.isBlank(anonymousId)) {
                throw new IllegalArgumentException(
                        "Parameter \"distinctId\" and parameter \"anonymousId\" cannot both be blank. "
                );
            }
            Long time = (Long) properties.get("time");
            if (time == null) { time = System.currentTimeMillis(); }
            String format = "User \"%s\" performed \"%s\" operation in \"%s\". ";
            String user = StringUtils.isNotBlank(distinctId) ? distinctId : anonymousId;
            log.info(String.format(format, user, eventName, DateUtils.format(time)));
        }
        catch (Exception e) {
            log.error(getClass().getSimpleName() + ": An error has occurred. ", e);
        }
    }

}

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
    public void addEvent(String event, String type, Long time, String userId, String anonymousId, Map<String, Object> properties) {
        try {
            Assert.notBlank(event, "Parameter \"event\" must not blank. ");
            if (StringUtils.isBlank(userId) && StringUtils.isBlank(anonymousId)) {
                throw new IllegalArgumentException(
                        "Parameter \"userId\" and parameter \"anonymousId\" cannot both be blank. "
                );
            }
            if (time == null) { time = System.currentTimeMillis(); }
            String format = "User \"%s\" performed \"%s\" operation in \"%s\". ";
            String user = StringUtils.isNotBlank(userId) ? userId : anonymousId;
            log.info(String.format(format, user, event, DateUtils.format(time)));
        }
        catch (Exception e) {
            log.error(getClass().getSimpleName() + ": An error has occurred. ", e);
        }
    }

}

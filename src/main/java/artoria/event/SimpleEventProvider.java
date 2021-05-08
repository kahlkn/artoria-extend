package artoria.event;

import artoria.logging.Logger;
import artoria.logging.LoggerFactory;
import artoria.time.DateUtils;
import artoria.util.StringUtils;

import java.util.Map;

public class SimpleEventProvider extends AbstractEventProvider {
    private static Logger log = LoggerFactory.getLogger(SimpleEventProvider.class);

    @Override
    protected void push(Map<String, Object> eventRecord) {
        String anonymousId = (String) eventRecord.get("anonymousId");
        String distinctId = (String) eventRecord.get("distinctId");
        String name = (String) eventRecord.get("name");
        Long time = (Long) eventRecord.get("time");
        String format = "User \"%s\" performed \"%s\" operation in \"%s\". ";
        String user = StringUtils.isNotBlank(distinctId) ? distinctId : anonymousId;
        log.info(String.format(format, user, name, DateUtils.format(time)));
    }

}

package artoria.event;

import artoria.identifier.IdentifierUtils;
import org.junit.Test;

import java.util.Arrays;

public class EventUtilsTest {

    @Test
    public void test1() {
        ((SimpleEventProvider) EventUtils.getEventProvider())
                .setShowPropertyNames(Arrays.asList("appId", "serverId"));
        EventUtils.record()
                .setName("TEST_EVENT_1")
                .setType("system")
                .setTime(System.currentTimeMillis())
                .setDistinctId("u10000001")
                .setAnonymousId(IdentifierUtils.nextStringIdentifier())
                .setProperty("serverId", "SER1001")
                .setProperty("appId", "APP101812")
                .submit();
    }

    @Test
    public void test2() {
        EventUtils.record()
                .setName("TEST_EVENT_2")
                .setDistinctId("u10000001")
                .setProperty("serverId", "SER1009")
                .setProperty("appId", "APP101812");
        // Other code.
        // Other code.
        EventUtils.record()
                .setProperty("ip", "192.168.1.1")
                .setProperty("geo", "121.59")
                .removeProperty("appId");
        // Other code.
        // Other code.
        System.out.println(EventUtils.record().getProperty("ip"));
        EventUtils.submit();
        EventUtils.clear();
    }

    @Test
    public void test3() {
        EventUtils.record("record1").setName("TEST_EVENT_3").setDistinctId("u10000001");
        // Other code.
        // Other code.
        EventUtils.record().setName("TEST_EVENT_3").setDistinctId("u10000002").submit();
        // Other code.
        // Other code.
        EventUtils.record("record1").setProperty("ip", "192.168.1.1").submit();
    }

}

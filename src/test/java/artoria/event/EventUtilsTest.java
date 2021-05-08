package artoria.event;

import artoria.identifier.IdentifierUtils;
import org.junit.Test;

public class EventUtilsTest {

    @Test
    public void test1() {
        EventUtils.record()
                .setName("A_TEST_EVENT")
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
                .setName("A_TEST_EVENT")
                .setDistinctId("u10000001")
                .setProperty("serverId", "SER1009")
                .setProperty("appId", "APP101812");
        EventUtils.record()
                .setProperty("ip", "192.168.1.1")
                .setProperty("geo", "121.59")
                .removeProperty("appId");
        System.out.println(EventUtils.record().getProperty("ip"));
        EventUtils.submit();
        EventUtils.clear();
    }

    @Test
    public void test3() {
        EventUtils.submit("A_TEST_EVENT", null, "u10000001", null, null);
        EventUtils.record("1").setEventName("A_TEST_EVENT").setDistinctId("u10000001");
        EventUtils.record().setName("A_TEST_EVENT1").setDistinctId("u10000002").submit();
        EventUtils.record("1").submit();
    }

}

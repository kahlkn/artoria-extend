package artoria.cache;

import artoria.util.ThreadUtils;
import org.junit.Test;

import static artoria.common.Constants.*;

public class CaffeineCacheTest {
    private static Cache cache1;
    private static Cache cache2;
    private static Cache cache3;

    static {
        cache1 = new CaffeineCache("cache1", ZERO, ONE_THOUSAND, ZERO);
        cache2 = new CaffeineCache("cache2", ZERO, ZERO, ONE_THOUSAND);
        cache3 = new CaffeineCache("cache3", TWO, ZERO, ZERO);
    }

    @Test
    public void test1() {
        cache1.put("test1", "test1-value");
        System.out.println(cache1.get("test1"));
        ThreadUtils.sleepQuietly(ONE_THOUSAND);
        System.out.println(cache1.get("test1"));
    }

    @Test
    public void test2() {
        cache2.put("test2", "test2-value");
        ThreadUtils.sleepQuietly(EIGHT_HUNDRED);
        System.out.println(cache2.get("test2"));
        ThreadUtils.sleepQuietly(EIGHT_HUNDRED);
        System.out.println(cache2.get("test2"));
        ThreadUtils.sleepQuietly(ONE_THOUSAND);
        System.out.println(cache2.get("test2"));
    }

    @Test
    public void test3() {
        cache3.put("test3-1", "test3-1-value");
        cache3.put("test3-2", "test3-2-value");
        System.out.println(cache3.get("test3-1"));
        System.out.println(cache3.get("test3-2"));
        cache3.put("test3-3", "test3-3-value");
        System.out.println("----");
        ThreadUtils.sleepQuietly(ONE_THOUSAND);
        System.out.println(cache3.get("test3-3"));
        System.out.println(cache3.get("test3-2"));
        System.out.println(cache3.get("test3-1"));
    }

}

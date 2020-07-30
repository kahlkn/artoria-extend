package artoria.util;

import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Ignore
public class HttpClientUtilsTest {
    private String testUrl = "https://bing.com";
    private String testUrl1 = "https://github.com";
    private String testUrl2 = "https://wordpress.com";

    @Test
    public void test1() throws IOException {
        System.out.println(HttpClientUtils.get(testUrl));
        System.out.println(HttpClientUtils.get(testUrl1));
        System.out.println(HttpClientUtils.get(testUrl2));
    }

    @Test
    public void test2() throws IOException {
        Map<String, String> map = new HashMap<String, String>();
        map.put("aaaa", "123");
        map.put("bbbb", "456");
        System.out.println(HttpClientUtils.post(testUrl, map));
    }

}

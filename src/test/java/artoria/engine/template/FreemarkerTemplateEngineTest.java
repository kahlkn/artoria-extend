package artoria.engine.template;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static artoria.common.Constants.UTF_8;

public class FreemarkerTemplateEngineTest {
    private String source = "This is test string \"${testStr}\", \nTest string is \"${testStr}\". ";
    private String source1 = "You name is \"${data.name}\", \nAnd you age is \"${data.age}\". ";
    private Map<String, Object> input = new HashMap<String, Object>();

    @Before
    public void init() {
        input.put("testStr", "hello, world! ");
        input.put("nullVal", null);
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("name", "zhangsan");
        data.put("age", "19");
        input.put("data", data);
        TemplateUtils.setTemplateEngine(new FreemarkerTemplateEngine());
    }

    @Test
    public void test1() throws Exception {
        System.out.println(TemplateUtils.renderToString(input, "source", source));
        System.out.println(TemplateUtils.renderToString(input, "source1", source1));
        System.out.println();
        System.out.println(TemplateUtils.renderToString("testFreemarker.ftl", UTF_8, input));
    }

}

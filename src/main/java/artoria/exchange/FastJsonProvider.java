package artoria.exchange;

import com.alibaba.fastjson.JSON;

import java.lang.reflect.Type;

/**
 * Json provider simple implement by fastjson.
 * @author Kahle
 */
public class FastJsonProvider implements JsonProvider {
    private boolean prettyFormat;

    public FastJsonProvider() {

        this(false);
    }

    public FastJsonProvider(boolean prettyFormat) {

        this.prettyFormat = prettyFormat;
    }

    @Override
    public boolean getPrettyFormat() {

        return this.prettyFormat;
    }

    @Override
    public String toJsonString(Object object) {

        return JSON.toJSONString(object, prettyFormat);
    }

    @Override
    public <T> T parseObject(String jsonString, Type type) {

        return JSON.parseObject(jsonString, type);
    }

}

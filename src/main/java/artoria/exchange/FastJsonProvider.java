package artoria.exchange;

import artoria.util.ArrayUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static artoria.common.Constants.ZERO;
import static artoria.exchange.JsonFormat.PRETTY_FORMAT;

/**
 * Json provider simple implement by fastjson.
 * @author Kahle
 */
public class FastJsonProvider implements JsonProvider {

    protected Feature[] deserializerFeatures(JsonFeature[] features) {

        return new Feature[ZERO];
    }

    protected SerializerFeature[] serializerFeatures(JsonFeature[] features) {
        List<SerializerFeature> list = new ArrayList<SerializerFeature>();
        if (ArrayUtils.isEmpty(features)) {
            return list.toArray(new SerializerFeature[ZERO]);
        }
        for (JsonFeature jsonFeature : features) {
            if (jsonFeature == null) { continue; }
            if (jsonFeature instanceof JsonFormat
                    && PRETTY_FORMAT.equals(jsonFeature)) {
                list.add(SerializerFeature.PrettyFormat);
            }
        }
        return list.toArray(new SerializerFeature[ZERO]);
    }

    @Override
    public String toJsonString(Object object, JsonFeature... features) {

        return JSON.toJSONString(object, serializerFeatures(features));
    }

    @Override
    public <T> T parseObject(String jsonString, Type type, JsonFeature... features) {

        return JSON.parseObject(jsonString, type, deserializerFeatures(features));
    }

}

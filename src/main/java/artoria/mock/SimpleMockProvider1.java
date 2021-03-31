package artoria.mock;

import artoria.util.ArrayUtils;
import artoria.util.StringUtils;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.emptyMap;

public class SimpleMockProvider1 extends SimpleMockProvider {

    protected Map<Class, ClassMockerConfig> parseFeatures(MockFeature[] features) {
        if (ArrayUtils.isEmpty(features)) { return emptyMap(); }
        Map<Class, ClassMockerConfig> map = new HashMap<Class, ClassMockerConfig>();
        for (MockFeature feature : features) {
            if (feature == null) { continue; }
            if (feature instanceof ClassMockerConfig) {
                ClassMockerConfig config = (ClassMockerConfig) feature;
                Class<?> type = config.getType();
                map.put(type, config);
            }
        }
        return map;
    }

    @Override
    protected Object mockClassData(Class<?> attrType, String attrName
            , MockFeature[] features, int nested, Class<?> originalType, Type... genericTypes) {
        if (StringUtils.isNotBlank(attrName) && originalType != null) {
            Map<Class, ClassMockerConfig> map = parseFeatures(features);
            ClassMockerConfig config = map.get(originalType);
            if (config!=null&&config.getMockerMap().get(attrName)!=null) {
                Mocker mocker = config.getMockerMap().get(attrName);
                return mocker.mock();
            }
        }
        return super.mockClassData(attrType, attrName, features, nested, originalType, genericTypes);
    }

}

package artoria.mock;

import java.util.Map;

public class ClassMockerConfig implements MockFeature {
    private Map<String, Mocker> mockerMap;
    private Class<?> type;

    public Map<String, Mocker> getMockerMap() {
        return mockerMap;
    }

    public void setMockerMap(Map<String, Mocker> mockerMap) {
        this.mockerMap = mockerMap;
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

}

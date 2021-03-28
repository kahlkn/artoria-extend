package artoria.mock;

import com.github.jsonzou.jmockdata.DataConfig;
import com.github.jsonzou.jmockdata.MockConfig;
import com.github.jsonzou.jmockdata.mocker.BaseMocker;

import java.lang.reflect.Type;

public class JMockDataProvider implements MockProvider {

    @Override
    public <T> T mock(Type type, MockFeature... features) {
        MockConfig mockConfig = new MockConfig();
        mockConfig.init(type);
        DataConfig config = mockConfig.globalDataConfig();
        return new BaseMocker<T>(type).mock(config);
    }

}

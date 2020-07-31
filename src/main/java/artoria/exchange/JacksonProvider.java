package artoria.exchange;

import artoria.exception.ExceptionUtils;
import artoria.util.ArrayUtils;
import artoria.util.Assert;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.lang.reflect.Type;

import static artoria.exchange.JsonFormat.PRETTY_FORMAT;

public class JacksonProvider implements JsonProvider {
    private ObjectMapper objectMapper;

    public JacksonProvider() {

        this(new ObjectMapper());
    }

    public JacksonProvider(ObjectMapper objectMapper) {
        Assert.notNull(objectMapper, "Parameter \"objectMapper\" must not null. ");
        this.objectMapper = objectMapper;
    }

    @Override
    public String toJsonString(Object object, JsonFeature... features) {
        try {
            if (ArrayUtils.isEmpty(features)) {
                return objectMapper.writeValueAsString(object);
            }
            ObjectWriter objectWriter = null;
            for (JsonFeature jsonFeature : features) {
                if (jsonFeature == null) { continue; }
                if (jsonFeature instanceof JsonFormat
                        && PRETTY_FORMAT.equals(jsonFeature)) {
                    objectWriter = objectMapper.writerWithDefaultPrettyPrinter();
                }
            }
            if (objectWriter == null) {
                objectWriter = objectMapper.writer();
            }
            return objectWriter.writeValueAsString(object);
        }
        catch (Exception e) {
            throw ExceptionUtils.wrap(e);
        }
    }

    @Override
    public <T> T parseObject(String jsonString, Type type, JsonFeature... features) {
        try {
            TypeFactory typeFactory = objectMapper.getTypeFactory();
            JavaType javaType = typeFactory.constructType(type);
            return objectMapper.readValue(jsonString, javaType);
        }
        catch (Exception e) {
            throw ExceptionUtils.wrap(e);
        }
    }

}

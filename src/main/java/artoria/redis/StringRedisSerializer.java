package artoria.redis;

import artoria.convert.TypeConvertUtils;
import artoria.util.Assert;
import artoria.util.StringUtils;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

import java.nio.charset.Charset;

import static artoria.common.Constants.UTF_8;

public class StringRedisSerializer implements RedisSerializer<Object> {
    private final Charset charset;

    public StringRedisSerializer() {

        this(UTF_8);
    }

    public StringRedisSerializer(Charset charset) {
        Assert.notNull(charset, "Parameter \"charset\" must not null. ");
        this.charset = charset;
    }

    public StringRedisSerializer(String charset) {
        Assert.notBlank(charset, "Parameter \"charset\" must not blank. ");
        this.charset = Charset.forName(charset);
    }

    @Override
    public byte[] serialize(Object object) throws SerializationException {
        if (object == null) { return null; }
        String string = (String) TypeConvertUtils.convert(object, String.class);
        if (StringUtils.isBlank(string)) { return null; }
        return string.getBytes(charset);
    }

    @Override
    public Object deserialize(byte[] bytes) throws SerializationException {
        if (bytes == null) { return null; }
        return new String(bytes, charset);
    }

}

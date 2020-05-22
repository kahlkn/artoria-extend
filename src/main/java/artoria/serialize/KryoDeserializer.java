package artoria.serialize;

import artoria.util.Assert;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.pool.KryoCallback;
import com.esotericsoftware.kryo.pool.KryoPool;

import java.io.IOException;
import java.io.InputStream;

/**
 * Kryo deserializer.
 * @author Kahle
 */
public class KryoDeserializer implements Deserializer<Object> {
    private KryoPool kryoPool;

    public KryoDeserializer(KryoPool kryoPool) {

        setKryoPool(kryoPool);
    }

    public KryoPool getKryoPool() {

        return kryoPool;
    }

    public void setKryoPool(KryoPool kryoPool) {
        Assert.notNull(kryoPool, "Parameter \"kryoPool\" must not null. ");
        this.kryoPool = kryoPool;
    }

    @Override
    public Object deserialize(final InputStream inputStream) throws IOException {
        Assert.notNull(inputStream, "Parameter \"inputStream\" must not null. ");
        return kryoPool.run(new KryoCallback<Object>() {
            @Override
            public Object execute(Kryo kryo) {
                Input input = new Input(inputStream);
                return kryo.readClassAndObject(input);
            }
        });
    }

}

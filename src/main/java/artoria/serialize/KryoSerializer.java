package artoria.serialize;

import artoria.util.Assert;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.pool.KryoCallback;
import com.esotericsoftware.kryo.pool.KryoPool;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Kryo serializer.
 * @author Kahle
 */
public class KryoSerializer implements Serializer<Object> {
    private KryoPool kryoPool;

    public KryoSerializer(KryoPool kryoPool) {

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
    public void serialize(final Object object, final OutputStream outputStream) throws IOException {
        Assert.notNull(object, "Parameter \"object\" must not null. ");
        Assert.notNull(outputStream, "Parameter \"outputStream\" must not null. ");
        kryoPool.run(new KryoCallback<Object>() {
            @Override
            public Object execute(Kryo kryo) {
                Output output = new Output(outputStream);
                kryo.writeClassAndObject(output, object);
                output.flush();
                return null;
            }
        });
    }

}

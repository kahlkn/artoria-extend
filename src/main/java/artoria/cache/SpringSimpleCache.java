package artoria.cache;

import artoria.lang.ReferenceType;
import artoria.util.Assert;
import org.springframework.util.ConcurrentReferenceHashMap;

import java.util.Map;

import static artoria.common.Constants.FIFTY;
import static org.springframework.util.ConcurrentReferenceHashMap.ReferenceType.SOFT;
import static org.springframework.util.ConcurrentReferenceHashMap.ReferenceType.WEAK;

public class SpringSimpleCache extends SimpleCache {

    public SpringSimpleCache(String name) {

        super(name);
    }

    public SpringSimpleCache(String name, ReferenceType referenceType) {

        super(name, referenceType);
    }

    public SpringSimpleCache(String name, long capacity, ReferenceType referenceType) {

        super(name, capacity, referenceType);
    }

    public SpringSimpleCache(String name, long capacity, long timeToLive, ReferenceType referenceType) {

        super(name, capacity, timeToLive, referenceType);
    }

    public SpringSimpleCache(String name, long capacity, long timeToLive, long timeToIdle, ReferenceType referenceType) {

        super(name, capacity, timeToLive, timeToIdle, referenceType);
    }

    @Override
    protected Map<Object, ValueWrapper> buildStorage(ReferenceType referenceType) {
        Assert.isTrue(
            ReferenceType.SOFT.equals(referenceType) || ReferenceType.WEAK.equals(referenceType),
            "Parameter \"referenceType\" must be soft reference or weak reference. "
        );
        if (ReferenceType.SOFT.equals(referenceType)) {
            return new ConcurrentReferenceHashMap<Object, ValueWrapper>(FIFTY, SOFT);
        }
        else {
            return new ConcurrentReferenceHashMap<Object, ValueWrapper>(FIFTY, WEAK);
        }
    }

}

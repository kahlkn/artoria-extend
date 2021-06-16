package artoria.beans;

import artoria.convert.type1.ConversionProvider;
import artoria.convert.type1.ConversionUtils;
import artoria.util.Assert;

public class CglibBeanMapFactory implements BeanMapFactory {
    private final ConversionProvider conversionProvider;

    public CglibBeanMapFactory() {

        this(ConversionUtils.getConversionProvider());
    }

    public CglibBeanMapFactory(ConversionProvider conversionProvider) {
        Assert.notNull(conversionProvider, "Parameter \"conversionProvider\" must not null. ");
        this.conversionProvider = conversionProvider;
    }

    @Override
    public BeanMap getInstance() {

        return new CglibBeanMap(conversionProvider);
    }

    @Override
    public BeanMap getInstance(Object bean) {

        return new CglibBeanMap(conversionProvider, bean);
    }

}

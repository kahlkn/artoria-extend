package artoria.beans;

import artoria.convert.ConversionProvider;
import artoria.convert.ConversionUtils;
import artoria.util.Assert;

public class SpringCglibBeanMapFactory implements BeanMapFactory {
    private final ConversionProvider conversionProvider;

    public SpringCglibBeanMapFactory() {

        this(ConversionUtils.getConversionProvider());
    }

    public SpringCglibBeanMapFactory(ConversionProvider conversionProvider) {
        Assert.notNull(conversionProvider, "Parameter \"conversionProvider\" must not null. ");
        this.conversionProvider = conversionProvider;
    }

    @Override
    public BeanMap getInstance() {

        return new SpringCglibBeanMap(conversionProvider);
    }

    @Override
    public BeanMap getInstance(Object bean) {

        return new SpringCglibBeanMap(conversionProvider, bean);
    }

}

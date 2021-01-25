package artoria.option;

import artoria.util.Assert;
import artoria.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Option auto configuration.
 * @author Kahle
 */
@Configuration
@EnableConfigurationProperties({OptionProperties.class})
public class OptionAutoConfiguration {
    private static Logger log = LoggerFactory.getLogger(OptionAutoConfiguration.class);

    @Autowired
    public OptionAutoConfiguration(ApplicationContext context, OptionProperties properties) {
        OptionProperties.ProviderType providerType = properties.getProviderType();
        Assert.notNull(providerType, "Parameter \"providerType\" must not null. ");
        OptionProvider optionProvider = null;
        if (OptionProperties.ProviderType.JDBC.equals(providerType)) {
            optionProvider = handleJdbc(context, properties);
        }
        else if (OptionProperties.ProviderType.CUSTOM.equals(providerType)) {
            optionProvider = handleCustom(context, properties);
        }
        else {
        }
        if (optionProvider != null) {
            OptionUtils.setOptionProvider(optionProvider);
        }
    }

    protected OptionProvider handleJdbc(ApplicationContext context, OptionProperties properties) {
        OptionProperties.JdbcConfig jdbcConfig = properties.getJdbcConfig();
        Assert.notNull(jdbcConfig, "Parameter \"jdbcConfig\" must not null. ");
        JdbcTemplate jdbcTemplate = context.getBean(JdbcTemplate.class);
        String ownerColumnName = jdbcConfig.getOwnerColumnName();
        String nameColumnName = jdbcConfig.getNameColumnName();
        String valueColumnName = jdbcConfig.getValueColumnName();
        String tableName = jdbcConfig.getTableName();
        String whereContent = jdbcConfig.getWhereContent();
        OptionProvider optionProvider = new JdbcOptionProvider(jdbcTemplate,
                ownerColumnName, nameColumnName, valueColumnName, tableName, whereContent);
        /*if () {
            optionProvider = new CacheOptionProvider();
        }*/
        return optionProvider;
    }

    protected OptionProvider handleCustom(ApplicationContext context, OptionProperties properties) {
        OptionProperties.CustomConfig customConfig = properties.getCustomConfig();
        Assert.notNull(customConfig, "Parameter \"customConfig\" must not null. ");
        Class<? extends OptionProvider> beanType = customConfig.getSpringContextBeanType();
        String beanName = customConfig.getSpringContextBeanName();
        boolean notBlank = StringUtils.isNotBlank(beanName);
        OptionProvider optionProvider;
        if (notBlank && beanType == null) {
            optionProvider = context.getBean(beanName, OptionProvider.class);
        }
        else if (notBlank) {
            optionProvider = context.getBean(beanName, beanType);
        }
        else if (beanType != null) {
            optionProvider = context.getBean(beanType);
        }
        else {
            throw new IllegalArgumentException(
                "Configuration items \"spring-context-bean-name\" and \"spring-context-bean-type\" cannot both be empty. "
            );
        }
        return optionProvider;
    }

}

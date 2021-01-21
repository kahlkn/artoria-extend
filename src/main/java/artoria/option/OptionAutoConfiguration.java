package artoria.option;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
@EnableConfigurationProperties({OptionProperties.class})
public class OptionAutoConfiguration {
    private static Logger log = LoggerFactory.getLogger(OptionAutoConfiguration.class);

    @Autowired
    public OptionAutoConfiguration(ApplicationContext appContext, OptionProperties properties) {
        OptionProvider optionProvider = optionProvider(appContext, properties);
        if (optionProvider != null) {
            OptionUtils.setOptionProvider(optionProvider);
        }
    }

    protected OptionProvider optionProvider(ApplicationContext context, OptionProperties prop) {
        OptionProvider optionProvider;
        try {
            optionProvider = context.getBean(OptionProvider.class);
            return optionProvider;
        }
        catch (Exception e) {
            log.debug("Failed to get \"optionProvider\" from application context. ", e);
        }
        OptionProperties.ProviderType providerType = prop.getProviderType();
        if (OptionProperties.ProviderType.JDBC.equals(providerType)) {
            JdbcTemplate jdbcTemplate = context.getBean(JdbcTemplate.class);
            String ownerColumnName = prop.getOwnerColumnName();
            String nameColumnName = prop.getNameColumnName();
            String valueColumnName = prop.getValueColumnName();
            String tableName = prop.getTableName();
            String whereContent = prop.getWhereContent();
            optionProvider = new JdbcOptionProvider(jdbcTemplate,
                    ownerColumnName, nameColumnName, valueColumnName, tableName, whereContent);
        }
        else {
//            optionProvider = new SimpleOptionProvider();
            optionProvider = null;
        }
        return optionProvider;
    }

}

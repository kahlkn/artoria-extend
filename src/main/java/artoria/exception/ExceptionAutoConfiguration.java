package artoria.exception;

import artoria.util.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Exception auto configuration.
 * @author Kahle
 */
@Configuration
@AutoConfigureBefore({ErrorMvcAutoConfiguration.class})
@EnableConfigurationProperties({ExceptionProperties.class})
@ConditionalOnProperty(name = "artoria.exception.enabled", havingValue = "true")
@Import({DefaultExceptionHandler.class, DefaultErrorController.class})
public class ExceptionAutoConfiguration implements InitializingBean, DisposableBean {
    private static Logger log = LoggerFactory.getLogger(ExceptionAutoConfiguration.class);
    private final ErrorCodeProvider errorCodeProvider;
    private final ErrorHandler errorHandler;

    @Autowired
    public ExceptionAutoConfiguration(ApplicationContext context, ExceptionProperties properties) {
        ExceptionProperties.ProviderType providerType = properties.getProviderType();
        Assert.notNull(providerType, "Parameter \"providerType\" must not null. ");
        if (ExceptionProperties.ProviderType.SIMPLE.equals(providerType)) {
            errorCodeProvider = new SimpleErrorCodeProvider();
        }
        else if (ExceptionProperties.ProviderType.JDBC.equals(providerType)) {
            errorCodeProvider = handleJdbc(context, properties);
        }
        else {
            errorCodeProvider = new SimpleErrorCodeProvider();
        }
        Boolean internalErrorPage = properties.getInternalErrorPage();
        String baseTemplatePath = properties.getBaseTemplatePath();
        errorHandler = new SimpleErrorHandler(errorCodeProvider, internalErrorPage, baseTemplatePath);
    }

    protected ErrorCodeProvider handleJdbc(ApplicationContext context, ExceptionProperties properties) {
        ExceptionProperties.JdbcConfig jdbcConfig = properties.getJdbcConfig();
        Assert.notNull(jdbcConfig, "Parameter \"jdbcConfig\" must not null. ");
        JdbcTemplate jdbcTemplate = context.getBean(JdbcTemplate.class);
        String codeColumnName = jdbcConfig.getCodeColumnName();
        String descriptionColumnName = jdbcConfig.getDescriptionColumnName();
        String tableName = jdbcConfig.getTableName();
        String whereContent = jdbcConfig.getWhereContent();
        ErrorCodeProvider errorCodeProvider = new JdbcErrorCodeProvider(jdbcTemplate, tableName);
        ((JdbcErrorCodeProvider) errorCodeProvider).setCodeColumn(codeColumnName);
        ((JdbcErrorCodeProvider) errorCodeProvider).setDescriptionColumn(descriptionColumnName);
        return errorCodeProvider;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
    }

    @Override
    public void destroy() throws Exception {
    }

    @Bean
    @ConditionalOnMissingBean
    public ErrorCodeProvider errorCodeProvider() {

        return errorCodeProvider;
    }

    @Bean
    @ConditionalOnMissingBean
    public ErrorHandler errorHandler() {

        return errorHandler;
    }

}

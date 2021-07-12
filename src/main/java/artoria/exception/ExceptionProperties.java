package artoria.exception;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Exception properties.
 * @author Kahle
 */
@ConfigurationProperties(prefix = "artoria.exception")
public class ExceptionProperties {
    /**
     * Enabled default exception handler.
     */
    private Boolean enabled;
    /**
     * Enabled internal error page.
     */
    private Boolean internalErrorPage = true;
    /**
     * Error display page base template path.
     */
    private String baseTemplatePath = "/error";

    private ProviderType providerType = ProviderType.SIMPLE;

    private JdbcConfig jdbcConfig;

    public Boolean getEnabled() {

        return enabled;
    }

    public void setEnabled(Boolean enabled) {

        this.enabled = enabled;
    }

    public Boolean getInternalErrorPage() {

        return internalErrorPage;
    }

    public void setInternalErrorPage(Boolean internalErrorPage) {

        this.internalErrorPage = internalErrorPage;
    }

    public String getBaseTemplatePath() {

        return baseTemplatePath;
    }

    public void setBaseTemplatePath(String baseTemplatePath) {

        this.baseTemplatePath = baseTemplatePath;
    }

    public ProviderType getProviderType() {
        return providerType;
    }

    public void setProviderType(ProviderType providerType) {
        this.providerType = providerType;
    }

    public JdbcConfig getJdbcConfig() {
        return jdbcConfig;
    }

    public void setJdbcConfig(JdbcConfig jdbcConfig) {
        this.jdbcConfig = jdbcConfig;
    }

    public enum ProviderType {
        /**
         * Simple.
         */
        SIMPLE,
        /**
         * Jdbc.
         */
        JDBC,
        ;
    }

    public static class JdbcConfig {
        private String codeColumnName;
        private String descriptionColumnName;
        private String tableName;
        private String whereContent;

        public String getCodeColumnName() {
            return codeColumnName;
        }

        public void setCodeColumnName(String codeColumnName) {
            this.codeColumnName = codeColumnName;
        }

        public String getDescriptionColumnName() {
            return descriptionColumnName;
        }

        public void setDescriptionColumnName(String descriptionColumnName) {
            this.descriptionColumnName = descriptionColumnName;
        }

        public String getTableName() {
            return tableName;
        }

        public void setTableName(String tableName) {
            this.tableName = tableName;
        }

        public String getWhereContent() {
            return whereContent;
        }

        public void setWhereContent(String whereContent) {
            this.whereContent = whereContent;
        }
    }

}

package artoria.option;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Option properties.
 * @author Kahle
 */
@ConfigurationProperties(prefix = "artoria.option")
public class OptionProperties {
    private ProviderType providerType = ProviderType.SIMPLE;
    private JdbcConfig jdbcConfig;
    private CustomConfig customConfig;

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

    public CustomConfig getCustomConfig() {

        return customConfig;
    }

    public void setCustomConfig(CustomConfig customConfig) {

        this.customConfig = customConfig;
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
        /**
         * Custom.
         */
        CUSTOM,
        ;
    }

    public static class JdbcConfig {
        private String ownerColumnName;
        private String nameColumnName;
        private String valueColumnName;
        private String tableName;
        private String whereContent;

        public String getOwnerColumnName() {

            return ownerColumnName;
        }

        public void setOwnerColumnName(String ownerColumnName) {

            this.ownerColumnName = ownerColumnName;
        }

        public String getNameColumnName() {

            return nameColumnName;
        }

        public void setNameColumnName(String nameColumnName) {

            this.nameColumnName = nameColumnName;
        }

        public String getValueColumnName() {

            return valueColumnName;
        }

        public void setValueColumnName(String valueColumnName) {

            this.valueColumnName = valueColumnName;
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

    public static class CustomConfig {
        private Class<? extends OptionProvider> springContextBeanType;
        private String springContextBeanName;

        public Class<? extends OptionProvider> getSpringContextBeanType() {

            return springContextBeanType;
        }

        public void setSpringContextBeanType(Class<? extends OptionProvider> springContextBeanType) {

            this.springContextBeanType = springContextBeanType;
        }

        public String getSpringContextBeanName() {

            return springContextBeanName;
        }

        public void setSpringContextBeanName(String springContextBeanName) {

            this.springContextBeanName = springContextBeanName;
        }

    }

}

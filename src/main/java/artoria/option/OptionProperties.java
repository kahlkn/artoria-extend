package artoria.option;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "artoria.option")
public class OptionProperties {
    private ProviderType providerType = ProviderType.SIMPLE;
    private String ownerColumnName;
    private String nameColumnName;
    private String valueColumnName;
    private String tableName;
    private String whereContent;

    public ProviderType getProviderType() {

        return providerType;
    }

    public void setProviderType(ProviderType providerType) {

        this.providerType = providerType;
    }

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

    public enum ProviderType {
        /**
         * SIMPLE.
         */
        SIMPLE,
        /**
         * JDBC.
         */
        JDBC,
        ;
    }

}

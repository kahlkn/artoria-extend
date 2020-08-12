package artoria.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.concurrent.TimeUnit;

@ConfigurationProperties(prefix = "artoria.property")
public class PropertyProperties {
    private ProviderType providerType = ProviderType.SIMPLE;
    private ReloadType reloadType = ReloadType.NONE;
    private TimeUnit reloadPeriodUnit = TimeUnit.MINUTES;
    private Long reloadPeriod = 3L;
    private String groupColumnName;
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

    public ReloadType getReloadType() {

        return reloadType;
    }

    public void setReloadType(ReloadType reloadType) {

        this.reloadType = reloadType;
    }

    public TimeUnit getReloadPeriodUnit() {

        return reloadPeriodUnit;
    }

    public void setReloadPeriodUnit(TimeUnit reloadPeriodUnit) {

        this.reloadPeriodUnit = reloadPeriodUnit;
    }

    public Long getReloadPeriod() {

        return reloadPeriod;
    }

    public void setReloadPeriod(Long reloadPeriod) {

        this.reloadPeriod = reloadPeriod;
    }

    public String getGroupColumnName() {

        return groupColumnName;
    }

    public void setGroupColumnName(String groupColumnName) {

        this.groupColumnName = groupColumnName;
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
         * REDIS.
         */
        REDIS,
        ;
    }

    public enum ReloadType {
        /**
         * NONE.
         */
        NONE,
        /**
         * JDBC.
         */
        JDBC,
        /**
         * REDIS.
         */
        REDIS,
        ;
    }

}

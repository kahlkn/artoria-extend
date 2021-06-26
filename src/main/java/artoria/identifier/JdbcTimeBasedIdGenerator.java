package artoria.identifier;

import artoria.exception.ExceptionUtils;
import artoria.time.DateTime;
import artoria.time.DateUtils;
import artoria.util.Assert;
import artoria.util.CollectionUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.Nullable;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Map;

import static artoria.common.Constants.ONE;
import static artoria.common.Constants.ZERO;

public class JdbcTimeBasedIdGenerator implements LongIdentifierGenerator {
    private static final String SQL_QUERY_TEMPLATE = "SELECT `%s`, `%s` FROM `%s` WHERE `%s` = ? FOR UPDATE;";
    private static final String SQL_INSERT_TEMPLATE = "INSERT INTO `%s` (`%s`, `%s`, `%s`) VALUES (?, ?, ?);";
    private static final String SQL_UPDATE_TEMPLATE = "UPDATE `%s` SET `%s` = ? WHERE `%s` = ?;";
    private static final String SQL_UPDATE_TEMPLATE1 = "UPDATE `%s` SET `%s` = ?, `%s` = ? WHERE `%s` = ?;";
    private TransactionTemplate transactionTemplate;
    private JdbcTemplate jdbcTemplate;
    private String expireColumn;
    private String valueColumn;
    private String nameColumn;
    private String tableName;
    private int stepLength = 1;
    private String name;

    public int getStepLength() {

        return stepLength;
    }

    public void setStepLength(int stepLength) {

        this.stepLength = stepLength;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public JdbcTimeBasedIdGenerator(TransactionTemplate transactionTemplate, JdbcTemplate jdbcTemplate) {
        Assert.notNull(transactionTemplate, "Parameter \"transactionTemplate\" must not null. ");
        Assert.notNull(jdbcTemplate, "Parameter \"jdbcTemplate\" must not null. ");
        this.transactionTemplate = transactionTemplate;
        this.jdbcTemplate = jdbcTemplate;
    }

    public String getExpireColumn() {

        return expireColumn;
    }

    public void setExpireColumn(String expireColumn) {

        this.expireColumn = expireColumn;
    }

    public String getValueColumn() {

        return valueColumn;
    }

    public void setValueColumn(String valueColumn) {

        this.valueColumn = valueColumn;
    }

    public String getNameColumn() {

        return nameColumn;
    }

    public void setNameColumn(String nameColumn) {

        this.nameColumn = nameColumn;
    }

    public String getTableName() {

        return tableName;
    }

    public void setTableName(String tableName) {

        this.tableName = tableName;
    }

    private void insert(String name, Long value, Long expire) {
        String insertSql = String.format(SQL_INSERT_TEMPLATE, tableName, nameColumn, valueColumn, expireColumn);
        int effect = jdbcTemplate.update(insertSql, name, value, expire);
        if (effect != ONE) {
            throw new IllegalStateException("Failed to insert the value of identifier. ");
        }
    }

    private void update(String name, Long value, Long expire) {
        int effect;
        if (expire!=null) {
            String updateSql = String.format(SQL_UPDATE_TEMPLATE1, tableName, valueColumn, expireColumn, nameColumn);
            effect = jdbcTemplate.update(updateSql, value, expire, name);
        }
        else {
            String updateSql = String.format(SQL_UPDATE_TEMPLATE, tableName, valueColumn, nameColumn);
            effect = jdbcTemplate.update(updateSql, value, name);
        }
        if (effect != ONE) {
            throw new IllegalStateException("Failed to update the value of identifier. ");
        }
    }

    private List<Map<String, Object>> query(String name) {
        String querySql = String.format(SQL_QUERY_TEMPLATE, valueColumn, expireColumn, tableName, nameColumn);
        return jdbcTemplate.queryForList(querySql, name);
    }

    private Long getExpire() {
        DateTime dateTime = DateUtils.create().addDay(1).setHour(0).setMinute(0).setSecond(0).setMillisecond(0);
        return dateTime.getTimeInMillis();
    }

    private Long increment() {
        long stepLength = getStepLength();
        String name = getName();
        List<Map<String, Object>> mapList = query(name);
        Map<String, Object> result;
        Long val;
        if (CollectionUtils.isNotEmpty(mapList)) {
            result = mapList.get(ZERO);
            Long expire = (Long) result.get(expireColumn);
            long currentTimeMillis = System.currentTimeMillis();
            if (expire!=null&&expire>=ZERO&&currentTimeMillis>=expire) {
                val = stepLength;
                update(name, val, getExpire());
            }
            else {
                val = (Long) result.get(valueColumn);
                val = val != null
                        ? val + stepLength : stepLength;
                update(name, val, null);
            }
        }
        else {
            val = stepLength;
            insert(name, val, getExpire());
        }
        return val;
    }

    protected Long incrementAndGet() {
        return transactionTemplate.execute(new TransactionCallback<Long>() {
            @Override
            public Long doInTransaction(@Nullable TransactionStatus status) {
                if (status == null) {
                    throw new IllegalArgumentException(
                            "This is a mistake that should not have happened. "
                    );
                }
                try {
                    return increment();
                }
                catch (Exception e) {
                    status.setRollbackOnly();
                    throw ExceptionUtils.wrap(e);
                }
            }
        });
    }

    @Override
    public Object nextIdentifier() {

        return nextLongIdentifier();
    }

    @Override
    public Long nextLongIdentifier() {
        Assert.notBlank(name, "Parameter \"name\" must not blank. ");
        // Increment value.
        Long increment = incrementAndGet();
        Assert.notNull(increment,
                "Failed to invoke \"incrementAndGet\". "
        );
        /*
        * 2021365 999999
        *          86399
        * 7 + 6
        * 2021365058344
        * 2021 - 2020 = 1
        * 101 365 9999999
        *           86399
        *          172798
        * one second = 2
        * */
        DateTime dateTime = DateUtils.create();
        int year = (dateTime.getYear()-2000) + 100;
        int dayOfYear = dateTime.getDayOfYear();

        int hour = dateTime.getHour();
        int minute = dateTime.getMinute();
        int second = dateTime.getSecond();
        int millisecond = dateTime.getMillisecond();

        long tmpInt = (hour * 60 + minute) * 60 + second;

        tmpInt = tmpInt * 2 + (millisecond / 500);

        return year * 10000000000L + dayOfYear * 10000000L + tmpInt + increment;
    }

}

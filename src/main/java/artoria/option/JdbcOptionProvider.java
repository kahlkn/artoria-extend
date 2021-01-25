package artoria.option;

import artoria.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.*;

import static artoria.common.Constants.*;

/**
 * Jdbc option provider.
 * @author Kahle
 */
public class JdbcOptionProvider extends AbstractOptionProvider {
    private static Logger log = LoggerFactory.getLogger(JdbcOptionProvider.class);
    private JdbcTemplate jdbcTemplate;
    private String ownerColumnName;
    private String nameColumnName;
    private String valueColumnName;
    private String tableName;
    private String whereContent;

    public JdbcOptionProvider(JdbcTemplate jdbcTemplate,
                              String ownerColumnName,
                              String nameColumnName,
                              String valueColumnName,
                              String tableName,
                              String whereContent) {
        Assert.notBlank(ownerColumnName, "Parameter \"ownerColumnName\" must not blank. ");
        Assert.notBlank(valueColumnName, "Parameter \"valueColumnName\" must not blank. ");
        Assert.notBlank(nameColumnName, "Parameter \"nameColumnName\" must not blank. ");
        Assert.notBlank(tableName, "Parameter \"tableName\" must not blank. ");
        Assert.notNull(jdbcTemplate, "Parameter \"jdbcTemplate\" must not null. ");
        this.jdbcTemplate = jdbcTemplate;
        this.ownerColumnName = ownerColumnName;
        this.nameColumnName = nameColumnName;
        this.valueColumnName = valueColumnName;
        this.tableName = tableName;
        this.whereContent = whereContent;
    }

    private String handle(String whereSql, List<Object> args, String columnName, Object columnVal) {
        if (!ownerColumnName.equals(columnName) && ObjectUtils.isEmpty(columnVal)) {
            return whereSql;
        }
        if (StringUtils.isBlank(whereSql)) {
            whereSql = String.format("where `%s` = ?", columnName);
        }
        else {
            if (!whereSql.endsWith(BLANK_SPACE)) {
                whereSql += BLANK_SPACE;
            }
            whereSql += String.format("and `%s` = ?", columnName);
        }
        args.add(columnVal);
        return whereSql;
    }

    @Override
    public boolean containsOption(String owner, String name) {
        Assert.notBlank(name, "Parameter \"name\" must not blank. ");
        if (StringUtils.isBlank(owner)) { owner = EMPTY_STRING; }
        String selectSql = String.format("select count(0) from `%s`", tableName);
        List<Object> args = new ArrayList<Object>();
        String whereSql = whereContent;
        whereSql = handle(whereSql, args, ownerColumnName, owner);
        whereSql = handle(whereSql, args, nameColumnName, name);
        String sql = String.format("%s %s", selectSql, whereSql);
        Integer count = jdbcTemplate.queryForObject(sql, args.toArray(), Integer.class);
        return count != null && count > ZERO;
    }

    @Override
    public Map<String, Object> getOptions(String owner) {
        if (StringUtils.isBlank(owner)) { owner = EMPTY_STRING; }
        String selectSql = String.format(
                "select `%s` as `owner`, `%s` as `name`, `%s` as `value` from `%s`",
                ownerColumnName, nameColumnName, valueColumnName, tableName
        );
        List<Object> args = new ArrayList<Object>();
        String whereSql = whereContent;
        whereSql = handle(whereSql, args, ownerColumnName, owner);
        String sql = String.format("%s %s", selectSql, whereSql);
        List<Map<String, Object>> mapList = jdbcTemplate.queryForList(sql, args.toArray());
        if (CollectionUtils.isEmpty(mapList)) { return Collections.emptyMap(); }
        Map<String, Object> result = new HashMap<String, Object>(mapList.size());
        for (Map<String, Object> map : mapList) {
            if (MapUtils.isEmpty(map)) { continue; }
            String name = (String) map.get("name");
            if (StringUtils.isBlank(name)) { continue; }
            Object value = map.get("value");
            result.put(name, value);
        }
        return result;
    }

    @Override
    public Object getOption(String owner, String name, Object defaultValue) {
        Assert.notBlank(name, "Parameter \"name\" must not blank. ");
        if (StringUtils.isBlank(owner)) { owner = EMPTY_STRING; }
        String selectSql = String.format(
                "select `%s` as `owner`, `%s` as `name`, `%s` as `value` from `%s`",
                ownerColumnName, nameColumnName, valueColumnName, tableName
        );
        List<Object> args = new ArrayList<Object>();
        String whereSql = whereContent;
        whereSql = handle(whereSql, args, ownerColumnName, owner);
        whereSql = handle(whereSql, args, nameColumnName, name);
        String sql = String.format("%s %s", selectSql, whereSql);
        List<Map<String, Object>> mapList = jdbcTemplate.queryForList(sql, args.toArray());
        if (CollectionUtils.isEmpty(mapList)) { return defaultValue; }
        Map<String, Object> map = mapList.get(ZERO);
        if (MapUtils.isEmpty(map)) { return defaultValue; }
        Object value = map.get("value");
        return value != null ? value : defaultValue;
    }

    @Override
    public Object setOption(String owner, String name, Object value) {
        Assert.notBlank(name, "Parameter \"name\" must not blank. ");
        Assert.notNull(value, "Parameter \"value\" must not null. ");
        if (StringUtils.isBlank(owner)) { owner = EMPTY_STRING; }
        boolean containsOption = containsOption(owner, name);
        if (containsOption) {
            String updateSql = String.format("update `%s` set `%s` = ?", tableName, valueColumnName);
            List<Object> args = new ArrayList<Object>();
            args.add(value);
            String whereSql = whereContent;
            whereSql = handle(whereSql, args, ownerColumnName, owner);
            whereSql = handle(whereSql, args, nameColumnName, name);
            String sql = String.format("%s %s", updateSql, whereSql);
            int update = jdbcTemplate.update(sql, args.toArray());
            Assert.state(update == ONE, "Edit record failure. ");
        }
        else {
            String insertSql = String.format(
                    "insert into `%s` (`%s`, `%s`, `%s`) values (?, ?, ?)",
                    tableName, ownerColumnName, nameColumnName, valueColumnName
            );
            List<Object> args = new ArrayList<Object>();
            args.add(owner);
            args.add(name);
            args.add(value);
            int insert = jdbcTemplate.update(insertSql, args.toArray());
            Assert.state(insert == ONE, "Add record failure. ");
        }
        return null;
    }

    @Override
    public Object removeOption(String owner, String name) {
        Assert.notBlank(name, "Parameter \"name\" must not blank. ");
        if (StringUtils.isBlank(owner)) { owner = EMPTY_STRING; }
        String deleteSql = String.format("delete from `%s`", tableName);
        List<Object> args = new ArrayList<Object>();
        String whereSql = whereContent;
        whereSql = handle(whereSql, args, ownerColumnName, owner);
        whereSql = handle(whereSql, args, nameColumnName, name);
        String sql = String.format("%s %s", deleteSql, whereSql);
        int delete = jdbcTemplate.update(sql, args.toArray());
        Assert.state(delete == ONE, "Delete record failure. ");
        return null;
    }

}

package artoria.property;

import artoria.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static artoria.common.Constants.ONE_HUNDRED;
import static artoria.common.Constants.TWENTY;

public class JdbcPropertyLoader implements PropertyLoader {
    private static Logger log = LoggerFactory.getLogger(JdbcPropertyLoader.class);
    private JdbcTemplate jdbcTemplate;
    private String groupColumnName;
    private String nameColumnName;
    private String valueColumnName;
    private String tableName;
    private String whereContent;

    public JdbcPropertyLoader(JdbcTemplate jdbcTemplate,
                              String groupColumnName,
                              String nameColumnName,
                              String valueColumnName,
                              String tableName,
                              String whereContent) {
        this.jdbcTemplate = jdbcTemplate;
        this.groupColumnName = groupColumnName;
        this.nameColumnName = nameColumnName;
        this.valueColumnName = valueColumnName;
        this.tableName = tableName;
        this.whereContent = whereContent;
    }

    @Override
    public Map<String, Map<String, Object>> loadAll() {
        String sql = String.format(
                "select `%s` as 'group', `%s` as 'name', `%s` as 'value' from `%s` %s",
                groupColumnName, nameColumnName, valueColumnName, tableName, whereContent
        );
        List<Map<String, Object>> mapList = jdbcTemplate.queryForList(sql);
        Map<String, Map<String, Object>> result =
                new HashMap<String, Map<String, Object>>(ONE_HUNDRED);
        for (Map<String, Object> map : mapList) {
            if (map == null) { continue; }
            String group = (String) map.get("group");
            String name = (String) map.get("name");
            Object value = map.get("value");
            if (StringUtils.isBlank(group)) { continue; }
            if (StringUtils.isBlank(name)) { continue; }
            Map<String, Object> groupMap = result.get(group);
            if (groupMap == null) {
                groupMap = new HashMap<String, Object>(TWENTY);
                result.put(group, groupMap);
            }
            groupMap.put(name, value);
        }
        return result;
    }

}

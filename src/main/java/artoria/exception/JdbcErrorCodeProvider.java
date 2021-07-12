package artoria.exception;

import artoria.cache.Cache;
import artoria.cache.SimpleCache;
import artoria.common.SimpleErrorCode;
import artoria.lang.ReferenceType;
import artoria.util.CollectionUtils;
import artoria.util.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

import static artoria.common.Constants.ZERO;

public class JdbcErrorCodeProvider implements ErrorCodeProvider {
    private static final String SQL_QUERY_TEMPLATE = "SELECT `%s`, `%s` FROM `%s` WHERE `%s` = ?;";
    private Cache cache;
    private JdbcTemplate jdbcTemplate;
    private String descriptionColumn;
    private String codeColumn;
    private String tableName;

    public JdbcErrorCodeProvider(JdbcTemplate jdbcTemplate, String tableName) {
        this.jdbcTemplate = jdbcTemplate;
        this.tableName = tableName;
        SimpleCache simpleCache = new SimpleCache(
                "JdbcErrorCodeProvider", ZERO, 5*60*1000, ReferenceType.SOFT);
        simpleCache.setPrintLog(true);
        cache = simpleCache;
    }

    public String getDescriptionColumn() {
        return descriptionColumn;
    }

    public void setDescriptionColumn(String descriptionColumn) {
        this.descriptionColumn = descriptionColumn;
    }

    public String getCodeColumn() {
        return codeColumn;
    }

    public void setCodeColumn(String codeColumn) {
        this.codeColumn = codeColumn;
    }

    @Override
    public ErrorCode getInstance(String errorCode) {
        if (StringUtils.isBlank(errorCode)) { return null; }
        ErrorCode result = cache.get(errorCode, ErrorCode.class);
        if (result != null) { return result; }
        String querySql = String.format(SQL_QUERY_TEMPLATE, codeColumn, descriptionColumn, tableName, codeColumn);
        List<Map<String, Object>> mapList = jdbcTemplate.queryForList(querySql, errorCode);
        if (CollectionUtils.isEmpty(mapList)) { return null; }
        Map<String, Object> map = mapList.get(ZERO);
        String description = (String) map.get(descriptionColumn);
        String code = (String) map.get(codeColumn);
        if (StringUtils.isBlank(code)) { return null; }
        result = new SimpleErrorCode(code, description);
        cache.put(errorCode, result);
        return result;
    }

}

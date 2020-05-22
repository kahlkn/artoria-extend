package artoria.generator;

import artoria.util.Assert;
import artoria.util.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static artoria.common.Constants.LEFT_PARENTHESIS;
import static artoria.common.Constants.ZERO;
import static artoria.io.IOUtils.EOF;

/**
 * Type mapping of the database to Java code.
 * @author Kahle
 */
public class DatabaseTypeMapper {
    private static final Map<String, String> TYPE_MAPPINGS = new ConcurrentHashMap<String, String>();

    public static String addTypeMapping(String columnClassNameOrType, String javaClassName) {
        Assert.notBlank(columnClassNameOrType
                , "Parameter \"columnClassNameOrType\" must not blank. ");
        Assert.notBlank(javaClassName
                , "Parameter \"javaClassName\" must not blank. ");
        return TYPE_MAPPINGS.put(columnClassNameOrType, javaClassName);
    }

    public static String getTypeMapping(String columnType, String columnClassName) {
        Assert.notBlank(columnClassName
                , "Parameter \"columnClassName\" must not blank. ");
        if (StringUtils.isNotBlank(columnType)) {
            int index = columnType.indexOf(LEFT_PARENTHESIS);
            if (index != EOF) {
                columnType = columnType.substring(ZERO, index);
            }
            String result = TYPE_MAPPINGS.get(columnType);
            if (StringUtils.isNotBlank(result)) { return result; }
        }
        String result = TYPE_MAPPINGS.get(columnClassName);
        return result == null ? columnClassName : result;
    }

    public static String removeTypeMapping(String columnClassNameOrType) {
        Assert.notBlank(columnClassNameOrType
                , "Parameter \"columnClassNameOrType\" must not blank. ");
        return TYPE_MAPPINGS.remove(columnClassNameOrType);
    }

    static {
        // java.util.Data can not be returned
        // java.sql.Date, java.sql.Time, java.sql.Timestamp all extends java.util.Data so getDate can return the three types data
        TYPE_MAPPINGS.put("java.util.Date", "java.util.Date");
        // date, year
        TYPE_MAPPINGS.put("java.sql.Date", "java.util.Date");
        // time
        TYPE_MAPPINGS.put("java.sql.Time", "java.util.Date");
        // timestamp, datetime
        TYPE_MAPPINGS.put("java.sql.Timestamp", "java.util.Date");
        // binary, varbinary, tinyblob, blob, mediumblob, longblob
        // qjd project: print_info.content varbinary(61800);
        TYPE_MAPPINGS.put("[B", "byte[]");
        // varchar, char, enum, set, text, tinytext, mediumtext, longtext
        TYPE_MAPPINGS.put("java.lang.String", "java.lang.String");
        // int, integer, tinyint, smallint, mediumint
        TYPE_MAPPINGS.put("java.lang.Integer", "java.lang.Integer");
        // bigint
        TYPE_MAPPINGS.put("java.lang.Long", "java.lang.Long");
        // real, double
        TYPE_MAPPINGS.put("java.lang.Double", "java.lang.Double");
        // float
        TYPE_MAPPINGS.put("java.lang.Float", "java.lang.Float");
        // bit
        TYPE_MAPPINGS.put("java.lang.Boolean", "java.lang.Boolean");
        // decimal, numeric
        TYPE_MAPPINGS.put("java.math.BigDecimal", "java.math.BigDecimal");
        // unsigned bigint
        TYPE_MAPPINGS.put("java.math.BigInteger", "java.math.BigInteger");
        // short
        TYPE_MAPPINGS.put("java.lang.Short", "java.lang.Short");
        // object
        TYPE_MAPPINGS.put("java.lang.Object", "java.lang.Object");
    }

}

package artoria.generator;

import artoria.beans.BeanUtils;
import artoria.exception.ExceptionUtils;
import artoria.jdbc.ColumnMeta;
import artoria.jdbc.DatabaseClient;
import artoria.jdbc.TableMeta;
import artoria.template.Renderer;
import artoria.time.DateUtils;
import artoria.util.Assert;
import artoria.util.CollectionUtils;
import artoria.util.MapUtils;
import artoria.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.*;

import static artoria.common.Constants.*;
import static artoria.io.IOUtils.EOF;

/**
 * Java code generator.
 * @author Kahle
 */
public class JavaCodeGenerator implements Generator<Boolean>, Serializable {
    private static final String DEFAULT_XML_BEGIN_COVER_MARK
            = "<!-- **** (Start) This will be covered, please do not modify. **** -->";
    private static final String DEFAULT_XML_END_COVER_MARK
            = "<!-- **** (End) This will be covered, please do not modify. **** -->";
    private static final String DEFAULT_JAVA_BEGIN_COVER_MARK
            = "/* (Start) This will be covered, please do not modify. */";
    private static final String DEFAULT_JAVA_END_COVER_MARK
            = "/* (End) This will be covered, please do not modify. */";
    private static final String DEFAULT_TEMPLATE_NAME_ARRAY
            = "mapper_xml,mapper_java,entity_java,serviceImpl_java,service_java,controller_java,dto_java,vo_java";
    private static final String JAVA_TYPE = "javaType";
    private static final String SERVICEIMPL = "serviceimpl";
    private static final String CONTROLLER = "controller";
    private static final String SERVICE = "service";
    private static final String MAPPER = "mapper";
    private static final String ENTITY = "entity";
    private static final String DTO = "dto";
    private static final String VO = "vo";
    private static final String JAVA = "java";
    private static final String XML = "xml";
    private static Logger log = LoggerFactory.getLogger(JavaCodeGenerator.class);
    private Set<String> removedTableNamePrefixes = new HashSet<String>();
    private Set<String> reservedTables = new HashSet<String>();
    private Set<String> excludedTables = new HashSet<String>();
    private DatabaseClient databaseClient;
    private String templateCharset = DEFAULT_ENCODING_NAME;
    private String outputCharset = DEFAULT_ENCODING_NAME;
    private String baseTemplatePath = "classpath:templates/generator/java/default";
    private String templateExtensionName = ".txt";
    private String baseOutputPath;
    private String basePackageName;
    private Renderer renderer;
    private Map<String, Object> attributes = new HashMap<String, Object>();
    private Map<String, JavaCodeCreator> creatorMap = new HashMap<String, JavaCodeCreator>();

    public JavaCodeGenerator() {
        this.addAttribute("author", "artoria-extend");
        this.addAttribute("date", DateUtils.format(FULL_DATETIME_PATTERN));
    }

    public Set<String> getRemovedTableNamePrefixes() {

        return Collections.unmodifiableSet(removedTableNamePrefixes);
    }

    public JavaCodeGenerator addRemovedTableNamePrefixes(String... removedTableNamePrefixes) {
        Assert.notEmpty(removedTableNamePrefixes
                , "Parameter \"removedTableNamePrefixes\" must not empty. ");
        Collections.addAll(this.removedTableNamePrefixes, removedTableNamePrefixes);
        return this;
    }

    public Set<String> getReservedTables() {

        return Collections.unmodifiableSet(reservedTables);
    }

    public JavaCodeGenerator addReservedTables(String... reservedTables) {
        Assert.notEmpty(reservedTables
                , "Parameter \"reservedTables\" must not empty. ");
        Collections.addAll(this.reservedTables, reservedTables);
        return this;
    }

    public Set<String> getExcludedTables() {

        return Collections.unmodifiableSet(excludedTables);
    }

    public JavaCodeGenerator addExcludedTables(String... excludedTables) {
        Assert.notEmpty(excludedTables
                , "Parameter \"excludedTables\" must not empty. ");
        Collections.addAll(this.excludedTables, excludedTables);
        return this;
    }

    public DatabaseClient getDatabaseClient() {

        return databaseClient;
    }

    public JavaCodeGenerator setDatabaseClient(DatabaseClient databaseClient) {
        Assert.notNull(databaseClient
                , "Parameter \"databaseClient\" must not null. ");
        this.databaseClient = databaseClient;
        return this;
    }

    public String getTemplateCharset() {

        return templateCharset;
    }

    public JavaCodeGenerator setTemplateCharset(String templateCharset) {
        Assert.notBlank(templateCharset
                , "Parameter \"templateCharset\" must not blank. ");
        this.templateCharset = templateCharset;
        return this;
    }

    public String getOutputCharset() {

        return outputCharset;
    }

    public JavaCodeGenerator setOutputCharset(String outputCharset) {
        Assert.notBlank(outputCharset
                , "Parameter \"outputCharset\" must not blank. ");
        this.outputCharset = outputCharset;
        return this;
    }

    public String getBaseTemplatePath() {

        return baseTemplatePath;
    }

    public JavaCodeGenerator setBaseTemplatePath(String baseTemplatePath) {
        Assert.notBlank(baseTemplatePath
                , "Parameter \"baseTemplatePath\" must not blank. ");
        this.baseTemplatePath = baseTemplatePath;
        return this;
    }

    public String getTemplateExtensionName() {

        return templateExtensionName;
    }

    public JavaCodeGenerator setTemplateExtensionName(String templateExtensionName) {
        Assert.notBlank(templateExtensionName
                , "Parameter \"templateExtensionName\" must not blank. ");
        this.templateExtensionName = templateExtensionName;
        return this;
    }

    public String getBaseOutputPath() {

        return baseOutputPath;
    }

    public JavaCodeGenerator setBaseOutputPath(String baseOutputPath) {
        Assert.notBlank(baseOutputPath
                , "Parameter \"baseOutputPath\" must not blank. ");
        this.baseOutputPath = baseOutputPath;
        return this;
    }

    public String getBasePackageName() {

        return basePackageName;
    }

    public JavaCodeGenerator setBasePackageName(String basePackageName) {
        Assert.notBlank(basePackageName
                , "Parameter \"basePackageName\" must not blank. ");
        this.basePackageName = basePackageName;
        return this;
    }

    public Renderer getRenderer() {

        return renderer;
    }

    public JavaCodeGenerator setRenderer(Renderer renderer) {
        Assert.notNull(renderer
                , "Parameter \"renderer\" must not null. ");
        this.renderer = renderer;
        return this;
    }

    public JavaCodeGenerator addAttribute(String name, Object value) {
        attributes.put(name, value);
        return this;
    }

    public JavaCodeGenerator addAttributes(Map<String, Object> data) {
        attributes.putAll(data);
        return this;
    }

    public JavaCodeGenerator removeAttribute(String name) {
        attributes.remove(name);
        return this;
    }

    public JavaCodeGenerator clearAttribute() {
        attributes.clear();
        return this;
    }

    public Map<String, Object> getAttributes() {

        return Collections.unmodifiableMap(attributes);
    }

    public JavaCodeGenerator newCreator() {
        this.newCreator(DEFAULT_TEMPLATE_NAME_ARRAY);
        return this;
    }

    public JavaCodeGenerator newCreator(String templateNameArray) {
        Assert.notBlank(templateNameArray
                , "Parameter \"templateNameArray\" must not blank. ");
        String[] split = templateNameArray.split(COMMA);
        for (String templateName : split) {
            if (StringUtils.isBlank(templateName)) { continue; }
            this.newCreator(templateName, null);
        }
        return this;
    }

    public JavaCodeGenerator newCreator(String templateName, String businessPackageName) {
        Assert.notBlank(templateName, "Parameter \"templateName\" must not blank. ");
        JavaCodeCreator creator = new JavaCodeCreator();
        boolean havePackageName = StringUtils.isNotBlank(businessPackageName);
        if (templateName.toLowerCase().contains(MAPPER)) {
            businessPackageName = havePackageName ? businessPackageName : ".persistence.mapper";
            creator.setSkipExisted(false);
        }
        else if (templateName.toLowerCase().contains(SERVICEIMPL)) {
            businessPackageName = havePackageName ? businessPackageName : ".service.impl";
            creator.setSkipExisted(true);
        }
        else if (templateName.toLowerCase().contains(SERVICE)) {
            businessPackageName = havePackageName ? businessPackageName : ".service";
            creator.setSkipExisted(false);
        }
        else if (templateName.toLowerCase().contains(CONTROLLER)) {
            businessPackageName = havePackageName ? businessPackageName : ".controller";
            creator.setSkipExisted(true);
        }
        else if (templateName.toLowerCase().contains(ENTITY)) {
            businessPackageName = havePackageName ? businessPackageName : ".persistence.entity";
            creator.setSkipExisted(false);
        }
        else if (templateName.toLowerCase().contains(VO)) {
            businessPackageName = havePackageName ? businessPackageName : ".pojo.vo";
            creator.setSkipExisted(false);
        }
        else if (templateName.toLowerCase().contains(DTO)) {
            businessPackageName = havePackageName ? businessPackageName : ".pojo.dto";
            creator.setSkipExisted(false);
        }
        else {
            businessPackageName = EMPTY_STRING;
            creator.setSkipExisted(true);
        }
        if (templateName.toLowerCase().contains(XML)) {
            creator.setBeginCoverMark(DEFAULT_XML_BEGIN_COVER_MARK);
            creator.setEndCoverMark(DEFAULT_XML_END_COVER_MARK);
        }
        else if (templateName.toLowerCase().contains(JAVA)) {
            creator.setBeginCoverMark(DEFAULT_JAVA_BEGIN_COVER_MARK);
            creator.setEndCoverMark(DEFAULT_JAVA_END_COVER_MARK);
        }
        creator.setBusinessPackageName(businessPackageName);
        creator.setTemplateName(templateName);
        creatorMap.put(templateName, creator);
        return this;
    }

    public JavaCodeGenerator removeCreator(String templateName) {
        creatorMap.remove(templateName);
        return this;
    }

    public JavaCodeGenerator clearCreator() {
        creatorMap.clear();
        return this;
    }

    public JavaCodeCreator getCreator(String templateName) {

        return creatorMap.get(templateName);
    }

    public Map<String, JavaCodeCreator> getCreatorMap() {

        return Collections.unmodifiableMap(creatorMap);
    }

    protected Map<String, Object> tableMap(TableMeta tableMeta) {
        if (tableMeta == null) { return null; }
        Map<String, Object> result = BeanUtils.beanToMap(tableMeta);
        // Get the column list and create a column map list.
        List<Map<String, Object>> columnMapList = new ArrayList<Map<String, Object>>();
        List<ColumnMeta> columnMetaList = tableMeta.getColumnList();
        result.put("columnList", columnMapList);
        if (CollectionUtils.isEmpty(columnMetaList)) { return result; }
        // Create a map of the import classes.
        Map<String, String> importClassMap = new HashMap<String, String>(columnMetaList.size());
        Set<String> typeImportList = new HashSet<String>();
        result.put("typeImportList", typeImportList);
        // Processing column data.
        for (ColumnMeta columnMeta : columnMetaList) {
            // Create column map.
            if (columnMeta == null) { continue; }
            Map<String, Object> columnMap = BeanUtils.beanToMap(columnMeta);
            columnMapList.add(columnMap);
            // Set column field, getter and setter.
            String columnName = columnMeta.getName();
            String fieldName = StringUtils.underlineToCamel(columnName);
            columnMap.put("fieldName", StringUtils.uncapitalize(fieldName));
            String capFieldName = StringUtils.capitalize(fieldName);
            columnMap.put("getterName", GET + capFieldName);
            columnMap.put("setterName", SET + capFieldName);
            // Handle java type.
            String columnClassName = columnMeta.getClassName();
            String columnType = columnMeta.getType();
            String typeMapping =
                    DatabaseTypeMapper.getTypeMapping(columnType, columnClassName);
            columnClassName =
                    StringUtils.isNotBlank(typeMapping) ? typeMapping : columnClassName;
            if (StringUtils.isBlank(columnClassName)) { continue; }
            // Try to convert short java type.
            int index = columnClassName.lastIndexOf(DOT);
            if (index == EOF) {
                columnMap.put(JAVA_TYPE, columnClassName);
                continue;
            }
            String shortName = columnClassName.substring(index + ONE);
            String className = importClassMap.get(shortName);
            if (className != null && !className.equals(columnClassName)) {
                columnMap.put(JAVA_TYPE, columnClassName);
            }
            else {
                importClassMap.put(shortName, columnClassName);
                typeImportList.add(columnClassName);
                columnMap.put(JAVA_TYPE, shortName);
            }
        }
        return result;
    }

    protected List<Map<String, Object>> tableMapList(List<TableMeta> tableList) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        if (CollectionUtils.isEmpty(tableList)) { return result; }
        // Clipping template name.
        Map<String, JavaCodeCreator> creatorMap =
                new HashMap<String, JavaCodeCreator>(this.creatorMap.size());
        for (JavaCodeCreator creator : this.creatorMap.values()) {
            String templateName = creator.getTemplateName();
            int index = templateName.lastIndexOf(UNDERLINE);
            if (index != MINUS_ONE) {
                templateName = templateName.substring(ZERO, index);
            }
            templateName = StringUtils.uncapitalize(templateName);
            creatorMap.put(templateName, creator);
        }
        // Convert table meta list to table map list.
        for (TableMeta tableMeta : tableList) {
            if (tableMeta == null) { continue; }
            Map<String, Object> tableMap = this.tableMap(tableMeta);
            if (tableMap == null) { continue; }
            // Get the name of the entity.
            String entityName = tableMeta.getAlias();
            // Create class name, object name and package name.
            for (Map.Entry<String, JavaCodeCreator> entry : creatorMap.entrySet()) {
                JavaCodeCreator creator = entry.getValue();
                if (creator == null) { continue; }
                String basePackageName = creator.getBasePackageName();
                if (StringUtils.isBlank(basePackageName)) {
                    basePackageName = this.getBasePackageName();
                }
                String businessPackageName = creator.getBusinessPackageName();
                String typeName = entry.getKey();
                String backTypeName = StringUtils.capitalize(typeName);
                if (VO.equalsIgnoreCase(backTypeName)
                        || DTO.equalsIgnoreCase(backTypeName)) {
                    backTypeName = backTypeName.toUpperCase();
                }
                else if (ENTITY.equalsIgnoreCase(backTypeName)) {
                    backTypeName = EMPTY_STRING;
                }
                String className = entityName + backTypeName;
                tableMap.put(typeName + "ClassName", className);
                String objectName = StringUtils.uncapitalize(className);
                tableMap.put(typeName + "ObjectName", objectName);
                String packageName = basePackageName + businessPackageName;
                tableMap.put(typeName + "PackageName", packageName);
            }
            // Add to the result.
            result.add(tableMap);
        }
        return result;
    }

    protected List<TableMeta> tableMetaList() throws SQLException {
        DatabaseClient databaseClient = this.getDatabaseClient();
        Assert.notNull(databaseClient, "Parameter \"databaseClient\" must not null. ");
        List<TableMeta> tableList = databaseClient.getTableMetaList();
        if (CollectionUtils.isEmpty(tableList)) { return tableList; }
        // Table filtering and alias handling.
        List<TableMeta> result = new ArrayList<TableMeta>();
        for (TableMeta tableMeta : tableList) {
            String tableName = tableMeta.getName();
            if (!reservedTables.isEmpty() && !reservedTables.contains(tableName)) {
                continue;
            }
            if (!excludedTables.isEmpty() && excludedTables.contains(tableName)) {
                continue;
            }
            // Handle entity name (alias).
            String entityName = tableName;
            if (!this.removedTableNamePrefixes.isEmpty()) {
                for (String prefix : this.removedTableNamePrefixes) {
                    if (!entityName.startsWith(prefix)) { continue; }
                    entityName = entityName.substring(prefix.length());
                    break;
                }
            }
            if (StringUtils.isBlank(entityName)) {
                log.error("Table name \"{}\" handled is blank, so continue. ", tableName);
                continue;
            }
            entityName = StringUtils.underlineToCamel(entityName);
            entityName = StringUtils.capitalize(entityName);
            tableMeta.setAlias(entityName);
            result.add(tableMeta);
        }
        return result;
    }

    @Override
    public Boolean generate() throws GenerateException {
        if (MapUtils.isEmpty(creatorMap)) { return false; }
        List<Map<String, Object>> tableMapList;
        try {
            tableMapList = this.tableMapList(this.tableMetaList());
            for (JavaCodeCreator creator : creatorMap.values()) {
                // Run the creator's configuration first.
                Map<String, Object> attributes = new HashMap<String, Object>(this.getAttributes());
                attributes.putAll(creator.getAttributes());
                creator.addAttributes(attributes);
                creator.setTemplateCharset(this.getTemplateCharset());
                creator.setOutputCharset(this.getOutputCharset());
                if (StringUtils.isBlank(creator.getBaseTemplatePath())) {
                    creator.setBaseTemplatePath(this.getBaseTemplatePath());
                }
                if (StringUtils.isBlank(creator.getTemplateExtensionName())) {
                    creator.setTemplateExtensionName(this.getTemplateExtensionName());
                }
                if (StringUtils.isBlank(creator.getBaseOutputPath())) {
                    creator.setBaseOutputPath(this.getBaseOutputPath());
                }
                if (StringUtils.isBlank(creator.getBasePackageName())) {
                    creator.setBasePackageName(this.getBasePackageName());
                }
                if (creator.getRenderer() == null) {
                    creator.setRenderer(this.getRenderer());
                }
                creator.create(tableMapList);
            }
            return true;
        }
        catch (Exception e) {
            throw ExceptionUtils.wrap(e);
        }
    }

}

package artoria.generator;

import artoria.exception.ExceptionUtils;
import artoria.file.FileUtils;
import artoria.io.IOUtils;
import artoria.io.StringBuilderWriter;
import artoria.logging.Logger;
import artoria.logging.LoggerFactory;
import artoria.template.Renderer;
import artoria.time.DateUtils;
import artoria.util.Assert;
import artoria.util.ClassLoaderUtils;
import artoria.util.CloseUtils;
import artoria.util.StringUtils;

import java.io.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static artoria.common.Constants.*;
import static artoria.io.IOUtils.EOF;

/**
 * Java code creator.
 * @author Kahle
 */
public class JavaCodeCreator implements Serializable {
    private static final String CLASSPATH = "classpath:";
    private static Logger log = LoggerFactory.getLogger(JavaCodeCreator.class);
    private Map<String, Object> attributes = new HashMap<String, Object>();
    private String templateCharset = DEFAULT_ENCODING_NAME;
    private String outputCharset = DEFAULT_ENCODING_NAME;
    private String baseTemplatePath;
    private String templateName;
    private String templateExtensionName;
    private String baseOutputPath;
    private String basePackageName;
    private String businessPackageName = EMPTY_STRING;
    private Boolean skipExisted = false;
    private String beginCoverMark;
    private String endCoverMark;
    private Renderer renderer;
    private String templatePath;
    private String outputPath;
    private String templateContent;

    protected JavaCodeCreator() {
    }

    public Map<String, Object> getAttributes() {

        return Collections.unmodifiableMap(attributes);
    }

    public void addAttribute(String name, Object value) {

        attributes.put(name, value);
    }

    public void addAttributes(Map<String, Object> data) {

        attributes.putAll(data);
    }

    public void removeAttribute(String name) {

        attributes.remove(name);
    }

    public void clearAttribute() {

        attributes.clear();
    }

    public String getTemplateCharset() {

        return templateCharset;
    }

    public void setTemplateCharset(String templateCharset) {
        Assert.notBlank(templateCharset, "Parameter \"templateCharset\" must not blank. ");
        this.templateCharset = templateCharset;
    }

    public String getOutputCharset() {

        return outputCharset;
    }

    public void setOutputCharset(String outputCharset) {
        Assert.notBlank(outputCharset, "Parameter \"outputCharset\" must not blank. ");
        this.outputCharset = outputCharset;
    }

    public String getBaseTemplatePath() {

        return baseTemplatePath;
    }

    public void setBaseTemplatePath(String baseTemplatePath) {
        Assert.notBlank(baseTemplatePath, "Parameter \"baseTemplatePath\" must not blank. ");
        this.baseTemplatePath = baseTemplatePath;
    }

    public String getTemplateName() {

        return templateName;
    }

    public void setTemplateName(String templateName) {
        Assert.notBlank(templateName, "Parameter \"templateName\" must not blank. ");
        Assert.state(templateName.contains(UNDERLINE)
                , "Parameter \"templateName\" must contain underline. ");
        templateName = templateName.endsWith(DOT)
                ? templateName.substring(0, templateName.length() - 1)
                : templateName;
        this.templateName = templateName;
    }

    public String getTemplateExtensionName() {

        return templateExtensionName;
    }

    public void setTemplateExtensionName(String templateExtensionName) {
        Assert.notNull(templateExtensionName
                , "Parameter \"templateExtensionName\" must not null. ");
        templateExtensionName = templateExtensionName.startsWith(DOT)
                ? templateExtensionName
                : DOT + templateExtensionName;
        this.templateExtensionName = templateExtensionName.trim();
    }

    public String getBaseOutputPath() {

        return baseOutputPath;
    }

    public void setBaseOutputPath(String baseOutputPath) {
        Assert.notBlank(baseOutputPath, "Parameter \"baseOutputPath\" must not blank. ");
        this.baseOutputPath = baseOutputPath;
    }

    public String getBasePackageName() {

        return basePackageName;
    }

    public void setBasePackageName(String basePackageName) {
        Assert.notBlank(basePackageName, "Parameter \"basePackageName\" must not blank. ");
        basePackageName = basePackageName.endsWith(DOT)
                ? basePackageName.substring(0, basePackageName.length() - 1)
                : basePackageName;
        this.basePackageName = basePackageName.trim();
    }

    public String getBusinessPackageName() {

        return businessPackageName;
    }

    public void setBusinessPackageName(String businessPackageName) {
        Assert.notNull(businessPackageName, "Parameter \"businessPackageName\" must not null. ");
        businessPackageName = businessPackageName.startsWith(DOT)
                ? businessPackageName
                : DOT + businessPackageName;
        this.businessPackageName = businessPackageName.trim();
    }

    public Boolean getSkipExisted() {

        return skipExisted;
    }

    public void setSkipExisted(Boolean skipExisted) {
        Assert.notNull(skipExisted, "Parameter \"skipExisted\" must not null. ");
        this.skipExisted = skipExisted;
    }

    public String getBeginCoverMark() {

        return beginCoverMark;
    }

    public void setBeginCoverMark(String beginCoverMark) {
        Assert.notBlank(beginCoverMark, "Parameter \"beginCoverMark\" must not blank. ");
        this.beginCoverMark = beginCoverMark;
    }

    public String getEndCoverMark() {

        return endCoverMark;
    }

    public void setEndCoverMark(String endCoverMark) {
        Assert.notBlank(endCoverMark, "Parameter \"endCoverMark\" must not blank. ");
        this.endCoverMark = endCoverMark;
    }

    public Renderer getRenderer() {

        return renderer;
    }

    public void setRenderer(Renderer renderer) {
        Assert.notNull(renderer, "Parameter \"renderer\" must not null. ");
        this.renderer = renderer;
    }

    public String getTemplatePath() {
        if (StringUtils.isNotBlank(templatePath)) {
            return templatePath;
        }
        String baseTemplatePath = this.getBaseTemplatePath();
        String templateName = this.getTemplateName();
        String extensionName = this.getTemplateExtensionName();
        String fileName = templateName + extensionName;
        templatePath = new File(baseTemplatePath, fileName).toString();
        return templatePath;
    }

    public void setTemplatePath(String templatePath) {
        Assert.notBlank(templatePath, "Parameter \"templatePath\" must not blank. ");
        this.templatePath = templatePath;
    }

    public String getOutputPath() {
        if (StringUtils.isNotBlank(outputPath)) {
            return outputPath;
        }
        String baseOutputPath = this.getBaseOutputPath();
        String basePackageName = this.getBasePackageName();
        String businessPackageName = this.getBusinessPackageName();
        String packageName = basePackageName + businessPackageName;
        packageName = StringUtils.replace(packageName, DOT, SLASH);
        outputPath = new File(baseOutputPath, packageName).toString();
        return outputPath;
    }

    public void setOutputPath(String outputPath) {
        Assert.notBlank(outputPath, "Parameter \"outputPath\" must not blank. ");
        this.outputPath = outputPath;
    }

    public String getTemplateContent() {
        if (StringUtils.isNotBlank(templateContent)) {
            return templateContent;
        }
        String templatePath = this.getTemplatePath();
        InputStream in = null;
        try {
            in = templatePath.startsWith(CLASSPATH) ?
                    ClassLoaderUtils.getResourceAsStream(
                            templatePath.substring(CLASSPATH.length()), this.getClass()
                    ) :
                    new FileInputStream(templatePath);
            templateContent = IOUtils.toString(in, this.getTemplateCharset());
        }
        catch (IOException e) {
            throw ExceptionUtils.wrap(e);
        }
        finally {
            CloseUtils.closeQuietly(in);
        }
        return templateContent;
    }

    public void setTemplateContent(String templateContent) {
        Assert.notBlank(templateContent, "Parameter \"templateContent\" must not blank. ");
        this.templateContent = templateContent;
    }

    protected String filename(Map<String, Object> tableMap) {
        Assert.notNull(tableMap, "Parameter \"tableMap\" must not null. ");
        String templateName = this.getTemplateName();
        int index = templateName.lastIndexOf(UNDERLINE), length;
        String begin = templateName, end = EMPTY_STRING;
        if (index != -1 && index + 1 !=
                (length = templateName.length())) {
            begin = templateName.substring(0, index);
            end = templateName.substring(index + 1, length);
        }
        if (begin.endsWith(UNDERLINE)) {
            begin = begin.substring(0, begin.length() - 1);
        }
        begin = StringUtils.uncapitalize(begin);
        String className = (String) tableMap.get(begin + "ClassName");
        return className + DOT + end;
    }

    protected String replace(String generation, String fileContent) {
        Assert.notBlank(generation, "Parameter \"generation\" must not blank. ");
        if (StringUtils.isBlank(fileContent)) { return generation; }
        // Get cover mark.
        String beginCoverMark = this.getBeginCoverMark();
        String endCoverMark = this.getEndCoverMark();
        if (StringUtils.isBlank(beginCoverMark) || StringUtils.isBlank(endCoverMark)) {
            return generation;
        }
        // Variable definition.
        StringBuilder result = new StringBuilder(); String tmpFormat;
        int fileIndex = 0, generatedIndex = 0, count = 0, tmpFromIndex;
        do {
            // Look for the begin cover mark in the file content.
            int fileBegin = fileContent.indexOf(beginCoverMark, fileIndex);
            // If don't find it once.
            if (fileBegin == EOF && count == 0) {
                log.info("The file already exists and can not find begin cover mark. ");
                return null;
            }
            // No begin cover mark found, indicating that processing is complete.
            if (fileBegin == EOF) {
                int fileEnd = fileContent.indexOf(endCoverMark, fileIndex);
                if (fileEnd != EOF) {
                    tmpFormat = "The end cover mark should not exist after index {} in file content. ";
                    log.info(tmpFormat, fileIndex);
                    return null;
                }
                int generationBegin = generation.indexOf(beginCoverMark, generatedIndex);
                if (generationBegin != EOF) {
                    tmpFormat = "The begin cover mark should not exist after index {} in generated content. ";
                    log.info(tmpFormat, generatedIndex);
                    return null;
                }
                int generationEnd = generation.indexOf(endCoverMark, generatedIndex);
                if (generationEnd != EOF) {
                    tmpFormat = "The end cover mark should not exist after index {} in generated content. ";
                    log.info(tmpFormat, generatedIndex);
                    return null;
                }
                result.append(fileContent.substring(fileIndex, fileContent.length()));
                return result.toString();
            }
            // Look for the end cover mark in the file content.
            tmpFromIndex = fileBegin + beginCoverMark.length();
            int fileEnd = fileContent.indexOf(endCoverMark, tmpFromIndex);
            if (fileEnd == EOF) {
                log.info("Can not find end cover mark in file content. ");
                return null;
            }
            // Look for the begin cover mark in the generated content.
            int generationBegin = generation.indexOf(beginCoverMark, generatedIndex);
            if (generationBegin == EOF) {
                log.info("Can not find begin cover mark in generated content. ");
                return null;
            }
            // Look for the end cover mark in the generated content.
            tmpFromIndex = generationBegin + beginCoverMark.length();
            int generationEnd = generation.indexOf(endCoverMark, tmpFromIndex);
            if (generationEnd == EOF) {
                log.info("Can not find end cover mark in generated content. ");
                return null;
            }
            // The assembly results.
            result.append(fileContent.substring(fileIndex, fileBegin));
            result.append(generation.substring(generationBegin, generationEnd));
            result.append(endCoverMark);
            fileIndex = fileEnd + endCoverMark.length();
            generatedIndex = generationEnd + endCoverMark.length();
            count++;
        } while (true);
    }

    public void create(Map<String, Object> tableMap) throws IOException {
        Assert.notNull(tableMap, "Parameter \"tableMap\" must not null. ");
        // Get output directory.
        File outputDir = new File(this.getOutputPath());
        if (!outputDir.exists() && !outputDir.mkdirs()) {
            throw new IOException("Directory \"" + outputDir + "\" create failure. ");
        }
        // Get the detailed output path.
        String filename = this.filename(tableMap);
        File outputFile = new File(outputDir, filename);
        // Get variables.
        String tableName = (String) tableMap.get("name");
        String outputCharset = this.getOutputCharset();
        String templateName = this.getTemplateName();
        String templateContent = this.getTemplateContent();
        Renderer renderer = this.getRenderer();
        // Create template filled model.
        Map<String, Object> model = new HashMap<String, Object>(this.getAttributes());
        model.put("generatedTime", DateUtils.format(FILLED_DATE_PATTERN));
        model.put("table", tableMap);
        // Print log.
        String tmpString = "Generator \"{}\": rendering the java code corresponding to table \"{}\". ";
        log.info(tmpString, templateName, tableName);
        // Handle whether existing.
        if (outputFile.exists()) {
            if (this.getSkipExisted()) { return; }
            log.info("The file \"{}\" already exists, it will be try replace. ", outputFile.getName());
            // Generated content.
            Writer builderWriter = new StringBuilderWriter();
            renderer.render(model, builderWriter, filename, templateContent, null);
            String generation = builderWriter.toString();
            // Read file content.
            byte[] fileBytes = FileUtils.read(outputFile);
            String fileContent = new String(fileBytes, outputCharset);
            // Do replace.
            String outputStr = this.replace(generation, fileContent);
            // Write to file.
            if (outputStr == null) { return; }
            byte[] outputBytes = outputStr.getBytes(outputCharset);
            FileUtils.write(outputBytes, outputFile);
        }
        else {
            // Try create new file.
            if (!outputFile.createNewFile()) {
                throw new IOException("File \"" + outputDir + "\" create failure. ");
            }
            // Write to file.
            Writer writer = null;
            try {
                OutputStream output = new FileOutputStream(outputFile);
                writer = new OutputStreamWriter(output, outputCharset);
                renderer.render(model, writer, filename, templateContent, null);
            }
            finally {
                CloseUtils.closeQuietly(writer);
            }
        }
    }

    public void create(List<Map<String, Object>> tableMapList) throws IOException {
        Assert.notEmpty(tableMapList, "Parameter \"tableMapList\" must not empty. ");
        for (Map<String, Object> tableMap : tableMapList) {
            if (tableMap == null) { continue; }
            this.create(tableMap);
        }
    }

}

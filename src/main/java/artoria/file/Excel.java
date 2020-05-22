package artoria.file;

import artoria.beans.BeanUtils;
import artoria.exception.ExceptionUtils;
import artoria.io.IOUtils;
import artoria.time.DateUtils;
import artoria.util.*;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.*;

import static artoria.common.Constants.*;

/**
 * Excel tools and excel object.
 * @author Kahle
 */
public class Excel extends BinaryFile implements Table {
    public static final String XLSX = "xlsx";
    public static final String XLS = "xls";
    private final Map<String, String> propertiesMapping = new LinkedHashMap<String, String>();
    private final Map<String, String> headersMapping = new LinkedHashMap<String, String>();
    private int columnStartNumber = 0;
    private int rowStartNumber = 0;
    private Sheet currentSheet;
    private Workbook workbook;
    private String extension;
    private byte[] template;

    private void createWorkbook(InputStream inputStream) throws IOException {
        String extension = getExtension();
        boolean haveStream = inputStream != null;
        boolean haveTemplate = ArrayUtils.isNotEmpty(template);
        if (!haveStream && haveTemplate) {
            inputStream = new ByteArrayInputStream(template);
            haveStream = true;
        }
        if (XLS.equalsIgnoreCase(extension)) {
            workbook = haveStream
                    ? new HSSFWorkbook(inputStream) : new HSSFWorkbook();
        }
        else if (XLSX.equalsIgnoreCase(extension)) {
            workbook = haveStream
                    ? new XSSFWorkbook(inputStream) : new XSSFWorkbook();
        }
        else {
            throw new IllegalArgumentException(
                    "Excel object can only have \"xls\" or \"xlsx\" extensions. ");
        }
    }

    private List<Object> getRowContent(int rowNumber, Integer firstCellNumber, Integer lastCellNumber) {
        // Get current select sheet a line, rowNum begin 0, contained n.
        // And firstCellNum begin 0, not contain lastCellNum.
        List<Object> rowContent = new ArrayList<Object>();
        Row row = getCurrentSheet().getRow(rowNumber);
        if (row == null) { return new ArrayList<Object>(); }
        if (firstCellNumber == null) {
            firstCellNumber = (int) row.getFirstCellNum();
        }
        if (lastCellNumber == null) {
            lastCellNumber = (int) row.getLastCellNum();
        }
        for (int i = firstCellNumber; i < lastCellNumber; i++) {
            Cell cell = row.getCell(i);
            Object value = getCellValue(cell);
            rowContent.add(value);
        }
        return rowContent;
    }

    private Object getCellValue(Cell cell) {
        Object cellValue;
        if (cell == null) {
            return null;
        }
        CellType cellType = cell.getCellTypeEnum();
        switch (cellType) {
            case NUMERIC:
            case FORMULA: {
                if (DateUtil.isCellDateFormatted(cell)) {
                    cellValue = cell.getDateCellValue();
                }
                else {
                    cellValue = cell.getNumericCellValue();
                }
                break;
            }
            case BOOLEAN: {
                cellValue = cell.getBooleanCellValue();
                break;
            }
            default: {
                cellValue = cell.getStringCellValue();
            }
        }
        return cellValue;
    }

    private void setCellValue(Cell cell, Object value) {
        if (cell == null || value == null) {
            return;
        }
        if (value instanceof String) {
            cell.setCellValue((String) value);
        }
        else if (value instanceof Number) {
            cell.setCellValue(((Number) value).doubleValue());
        }
        else if (value instanceof RichTextString) {
            cell.setCellValue((RichTextString) value);
        }
        else if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        }
        else if (value instanceof Date) {
            cell.setCellValue(DateUtils.format((Date) value));
        }
        else {
            cell.setCellValue(value.toString());
        }
    }

    public Workbook getWorkbook() {
        if (workbook != null) {
            return workbook;
        }
        try {
            createWorkbook(null);
        }
        catch (IOException e) {
            throw ExceptionUtils.wrap(e);
        }
        return workbook;
    }

    public Excel setWorkbook(Workbook workbook) {
        Assert.notNull(workbook, "Parameter \"workbook\" must not null. ");
        this.workbook = workbook;
        if (workbook instanceof HSSFWorkbook) {
            setExtension(XLS);
        }
        else if (workbook instanceof XSSFWorkbook) {
            setExtension(XLSX);
        }
        else {
            throw new IllegalArgumentException(
                    "Parameter \"workbook\" is an unsupported implementation. ");
        }
        return this;
    }

    public int getNumberOfSheets() {
        // Like list size, if 3, index is 0, 1, 2.
        return getWorkbook().getNumberOfSheets();
    }

    public Excel createSheet() {
        currentSheet = getWorkbook().createSheet();
        return this;
    }

    public Excel createSheet(String sheetName) {
        currentSheet = getWorkbook().createSheet(sheetName);
        return this;
    }

    public Excel selectSheet(int index) {
        currentSheet = getWorkbook().getSheetAt(index);
        return this;
    }

    public Excel selectSheet(String sheetName) {
        currentSheet = getWorkbook().getSheet(sheetName);
        return this;
    }

    public Sheet getCurrentSheet() {
        if (currentSheet == null) {
            if (getWorkbook().getNumberOfSheets() == ZERO) {
                createSheet();
            }
            else {
                selectSheet(ZERO);
            }
        }
        return currentSheet;
    }

    public String getExtension() {

        return extension;
    }

    public void setExtension(String extension) {
        extension = extension == null
                ? null
                : extension.trim().toLowerCase();
        this.extension = extension;
    }

    @Override
    public long read(InputStream inputStream) throws IOException {
        Assert.notNull(inputStream, "Parameter \"inputStream\" must not null. ");
        byte[] byteArray = IOUtils.toByteArray(inputStream);
        createWorkbook(new ByteArrayInputStream(byteArray));
        return byteArray.length;
    }

    @Override
    public void write(OutputStream outputStream) throws IOException {
        Assert.notNull(outputStream, "Parameter \"outputStream\" must not null. ");
        getWorkbook().write(outputStream);
        outputStream.flush();
    }

    @Override
    public long readFromFile(File file) throws IOException {
        Assert.notNull(file, "Parameter \"file\" must not null. ");
        String fileString = file.toString();
        String extension = FilenameUtils.getExtension(fileString);
        setExtension(extension);
        return super.readFromFile(file);
    }

    @Override
    public long readFromClasspath(String subPath) throws IOException {
        String extension = FilenameUtils.getExtension(subPath);
        setExtension(extension);
        return super.readFromClasspath(subPath);
    }

    @Override
    public int getLastRowNumber() {
        // Get the last row number on the current select sheet.
        // And based 0, contained n.
        return getCurrentSheet().getLastRowNum();
    }

    @Override
    public int getLastCellNumber(int rowNumber) {
        Row row = getCurrentSheet().getRow(rowNumber);
        return row != null ? row.getLastCellNum() : ZERO;
    }

    @Override
    public List<Object> getRowContent(int rowNumber) {

        return getRowContent(rowNumber, null, null);
    }

    @Override
    public void setRowContent(int rowNumber, List<?> rowContent) {
        int len = rowContent.size();
        int lastRowNumber = getCurrentSheet().getLastRowNum();
        // Can get to lastRowNum.
        Row row = lastRowNumber != ZERO && lastRowNumber >= rowNumber
                ? getCurrentSheet().getRow(rowNumber)
                : getCurrentSheet().createRow(rowNumber);
        for (int i = ZERO; i < len; ++i) {
            int lastCellNum = row.getLastCellNum();
            // Can not get to lastCellNum.
            Cell cell = lastCellNum > i
                    ? row.getCell(i) : row.createCell(i);
            Object val = rowContent.get(i);
            setCellValue(cell, val);
        }
    }

    @Override
    public Object getCellContent(int rowNumber, int columnNumber) {
        Row row = getCurrentSheet().getRow(rowNumber);
        if (row == null) { return null; }
        Cell cell = row.getCell(columnNumber);
        return cell != null ? getCellValue(cell) : null;
    }

    @Override
    public void setCellContent(int rowNumber, int columnNumber, Object cellContent) {
        Row row = getCurrentSheet().getRow(rowNumber);
        if (row == null) {
            row = getCurrentSheet().createRow(rowNumber);
        }
        Cell cell = row.getCell(columnNumber);
        if (cell == null) {
            cell = row.createCell(columnNumber);
        }
        setCellValue(cell, cellContent);
    }

    @Override
    public int getRowStartNumber() {

        return rowStartNumber;
    }

    @Override
    public void setRowStartNumber(int rowStartNumber) {
        Assert.isTrue(rowStartNumber >= ZERO
                , "Parameter \"rowStartNumber\" must >= 0. ");
        this.rowStartNumber = rowStartNumber;
    }

    @Override
    public int getColumnStartNumber() {

        return columnStartNumber;
    }

    @Override
    public void setColumnStartNumber(int columnStartNumber) {
        Assert.isTrue(columnStartNumber >= ZERO
                , "Parameter \"columnStartNumber\" must >= 0. ");
        this.columnStartNumber = columnStartNumber;
    }

    @Override
    public byte[] getTemplate() {

        return template;
    }

    @Override
    public void setTemplate(byte[] template) {
        Assert.notEmpty(template, "Parameter \"template\" must not empty. ");
        this.template = template;
    }

    @Override
    public void addHeader(String headerName, String propertyName) {
        Assert.notBlank(propertyName, "Parameter \"propertyName\" must not blank. ");
        Assert.notBlank(headerName, "Parameter \"headerName\" must not blank. ");
        propertiesMapping.put(propertyName, headerName);
        headersMapping.put(headerName, propertyName);
    }

    @Override
    public void addHeaders(Map<?, ?> headers) {
        Assert.notEmpty(headers, "Parameter \"headers\" must not empty. ");
        for (Map.Entry<?, ?> entry : headers.entrySet()) {
            String key = entry.getKey() != null
                    ? entry.getKey().toString() : EMPTY_STRING;
            String val = entry.getValue() != null
                    ? entry.getValue().toString() : EMPTY_STRING;
            propertiesMapping.put(val, key);
            headersMapping.put(key, val);
        }
    }

    @Override
    public void removeHeaderByHeaderName(String headerName) {
        Assert.notNull(headerName, "Parameter \"headerName\" must not null. ");
        if (!headersMapping.containsKey(headerName)) { return; }
        String propertyName = headersMapping.get(headerName);
        propertiesMapping.remove(propertyName);
        headersMapping.remove(headerName);
    }

    @Override
    public void removeHeaderByPropertyName(String propertyName) {
        Assert.notNull(propertyName, "Parameter \"propertyName\" must not null. ");
        if (!propertiesMapping.containsKey(propertyName)) { return; }
        String headerName = propertiesMapping.get(propertyName);
        propertiesMapping.remove(propertyName);
        headersMapping.remove(headerName);
    }

    @Override
    public void clearHeaders() {
        headersMapping.clear();
        propertiesMapping.clear();
    }

    @Override
    public <T> List<T> toBeanList(Class<T> clazz) {
        Assert.notNull(clazz, "Parameter \"clazz\" must not null. ");
        List<Map<String, Object>> mapList = toMapList();
        return BeanUtils.mapToBeanInList(mapList, clazz);
    }

    @Override
    public <T> void fromBeanList(List<T> beanList) {
        Assert.notEmpty(beanList, "Parameter \"beanList\" must not empty. ");
        List<Map<String, Object>> mapList = BeanUtils.beanToMapInList(beanList);
        fromMapList(mapList);
    }

    @Override
    public List<Map<String, Object>> toMapList() {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        int lastRowNumber = getLastRowNumber();
        boolean haveHeaders = MapUtils.isNotEmpty(headersMapping);
        List<String> propertyList = new ArrayList<String>();
        boolean isFirst = true;
        for (int i = columnStartNumber; i <= lastRowNumber; i++) {
            List<Object> rowContent = getRowContent(i);
            if (CollectionUtils.isEmpty(rowContent)) { continue; }
            if (isFirst) {
                for (Object cellObj : rowContent) {
                    String cell = cellObj == null ? EMPTY_STRING : cellObj + EMPTY_STRING;
                    String property = haveHeaders ? headersMapping.get(cell) : cell;
                    property = StringUtils.isNotBlank(property) ? property : cell;
                    propertyList.add(property);
                }
                isFirst = false;
                continue;
            }
            Map<String, Object> map = new HashMap<String, Object>(TWENTY);
            int pLen = propertyList.size(), rowSize = rowContent.size();
            for (int j = rowStartNumber; j < pLen; j++) {
                Object cell = j < rowSize ? rowContent.get(j) : null;
                String key = propertyList.get(j);
                map.put(key, cell);
            }
            result.add(map);
        }
        return result;
    }

    @Override
    public void fromMapList(List<Map<String, Object>> mapList) {
        Assert.notEmpty(mapList, "Parameter \"mapList\" must not empty. ");
        try {
            if (workbook == null) {
                createWorkbook(null);
            }
        }
        catch (IOException e) {
            throw ExceptionUtils.wrap(e);
        }
        List<String> headerList = new ArrayList<String>();
        if (rowStartNumber != ZERO) {
            for (int i = ZERO; i < rowStartNumber; i++) {
                headerList.add(EMPTY_STRING);
            }
        }
        boolean haveHeaders = MapUtils.isNotEmpty(propertiesMapping);
        if (haveHeaders) {
            headerList.addAll(propertiesMapping.values());
        }
        else {
            Map<String, Object> first = CollectionUtils.firstNotNullElement(mapList);
            headerList.addAll(first.keySet());
        }
        if (columnStartNumber != ZERO) {
            for (int i = ZERO; i < columnStartNumber; i++) {
                setRowContent(i, new ArrayList<String>());
            }
        }
        int columnNumber = columnStartNumber;
        setRowContent(columnNumber, headerList);
        for (Map<String, Object> beanMap : mapList) {
            if (beanMap == null) { continue; }
            List<Object> row = new ArrayList<Object>();
            if (rowStartNumber != ZERO) {
                for (int i = ZERO; i < rowStartNumber; i++) {
                    row.add(EMPTY_STRING);
                }
            }
            if (haveHeaders) {
                for (String property : propertiesMapping.keySet()) {
                    Object val = beanMap.get(property);
                    row.add(val != null ? val : EMPTY_STRING);
                }
            }
            else {
                for (Object val : beanMap.values()) {
                    row.add(val != null ? val : EMPTY_STRING);
                }
            }
            setRowContent(++columnNumber, row);
        }
    }

}

package artoria.file;

import artoria.beans.BeanUtils;
import artoria.exception.ExceptionUtils;
import artoria.io.IOUtils;
import artoria.util.*;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

import static artoria.common.Constants.EMPTY_STRING;

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
    private byte[] template;

    private List<Object> getRowContent(int rowNumber, Integer firstCellNumber, Integer lastCellNumber) {
        // Get current select sheet a line, rowNum begin 0, contained n.
        // And firstCellNum begin 0, not contain lastCellNum.
        List<Object> rowContent = new ArrayList<Object>();
        Row row = this.getCurrentSheet().getRow(rowNumber);
        if (row == null) { return new ArrayList<Object>(); }
        if (firstCellNumber == null) {
            firstCellNumber = (int) row.getFirstCellNum();
        }
        if (lastCellNumber == null) {
            lastCellNumber = (int) row.getLastCellNum();
        }
        for (int i = firstCellNumber; i < lastCellNumber; i++) {
            Cell cell = row.getCell(i);
            Object value = this.getCellValue(cell);
            rowContent.add(value);
        }
        return rowContent;
    }

    private void createWorkbook(InputStream inputStream) throws IOException {
        String extension = this.getExtension();
        boolean haveStream = inputStream != null;
        boolean haveTemplate = ArrayUtils.isNotEmpty(template);
        if (!haveStream && haveTemplate) {
            inputStream = new ByteArrayInputStream(template);
            haveStream = true;
        }
        if (XLS.equalsIgnoreCase(extension)) {
            this.workbook = haveStream
                    ? new HSSFWorkbook(inputStream) : new HSSFWorkbook();
        }
        else if (XLSX.equalsIgnoreCase(extension)) {
            this.workbook = haveStream
                    ? new XSSFWorkbook(inputStream) : new XSSFWorkbook();
        }
        else {
            throw new IllegalArgumentException(
                    "Excel object can only have \"xls\" or \"xlsx\" extensions. ");
        }
        if (this.workbook.getNumberOfSheets() == 0) {
            this.createSheet();
        }
        else {
            this.selectSheet(0);
        }
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
        else {
            cell.setCellValue(value.toString());
        }
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

    public Workbook getWorkbook() {
        if (this.workbook != null) {
            return this.workbook;
        }
        try {
            this.createWorkbook(null);
        }
        catch (IOException e) {
            throw ExceptionUtils.wrap(e);
        }
        return this.workbook;
    }

    public Excel setWorkbook(Workbook workbook) {
        Assert.notNull(workbook, "Parameter \"workbook\" must not null. ");
        this.workbook = workbook;
        if (this.workbook instanceof HSSFWorkbook) {
            this.setExtension(XLS);
        }
        else if (this.workbook instanceof XSSFWorkbook) {
            this.setExtension(XLSX);
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
        this.currentSheet = getWorkbook().createSheet();
        return this;
    }

    public Excel createSheet(String sheetName) {
        this.currentSheet = getWorkbook().createSheet(sheetName);
        return this;
    }

    public Excel selectSheet(int index) {
        this.currentSheet = getWorkbook().getSheetAt(index);
        return this;
    }

    public Excel selectSheet(String sheetName) {
        this.currentSheet = getWorkbook().getSheet(sheetName);
        return this;
    }

    public Sheet getCurrentSheet() {
        Assert.state(this.currentSheet != null
                , "Create or select sheet first.");
        return this.currentSheet;
    }

    @Override
    public long read(InputStream inputStream) throws IOException {
        Assert.notNull(inputStream
                , "Parameter \"inputStream\" must not null. ");
        byte[] byteArray = IOUtils.toByteArray(inputStream);
        this.createWorkbook(new ByteArrayInputStream(byteArray));
        return byteArray.length;
    }

    @Override
    public void write(OutputStream outputStream) throws IOException {
        Assert.notNull(outputStream
                , "Parameter \"outputStream\" must not null. ");
        getWorkbook().write(outputStream);
        outputStream.flush();
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
        return row != null ? row.getLastCellNum() : 0;
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
        Row row = lastRowNumber != 0 && lastRowNumber >= rowNumber
                ? getCurrentSheet().getRow(rowNumber)
                : getCurrentSheet().createRow(rowNumber);
        for (int i = 0; i < len; ++i) {
            int lastCellNum = row.getLastCellNum();
            // Can not get to lastCellNum.
            Cell cell = lastCellNum > i
                    ? row.getCell(i) : row.createCell(i);
            Object val = rowContent.get(i);
            this.setCellValue(cell, val);
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
        if (row == null) { return; }
        Cell cell = row.getCell(columnNumber);
        if (cell == null) {
            cell = row.createCell(columnNumber);
        }
        this.setCellValue(cell, cellContent);
    }

    @Override
    public int getRowStartNumber() {

        return rowStartNumber;
    }

    @Override
    public void setRowStartNumber(int rowStartNumber) {
        Assert.state(rowStartNumber >= 0
                , "Parameter \"rowStartNumber\" must >= 0. ");
        this.rowStartNumber = rowStartNumber;
    }

    @Override
    public int getColumnStartNumber() {

        return columnStartNumber;
    }

    @Override
    public void setColumnStartNumber(int columnStartNumber) {
        Assert.state(columnStartNumber >= 0
                , "Parameter \"columnStartNumber\" must >= 0. ");
        this.columnStartNumber = columnStartNumber;
    }

    @Override
    public byte[] getTemplate() {

        return template;
    }

    @Override
    public void setTemplate(byte[] template) {
        Assert.notEmpty(template
                , "Parameter \"template\" must not empty. ");
        this.template = template;
    }

    @Override
    public void addHeader(String headerName, String propertyName) {
        Assert.notBlank(propertyName
                , "Parameter \"propertyName\" must not blank. ");
        Assert.notBlank(headerName
                , "Parameter \"headerName\" must not blank. ");
        this.propertiesMapping.put(propertyName, headerName);
        this.headersMapping.put(headerName, propertyName);
    }

    @Override
    public void addHeaders(Map<?, ?> headers) {
        Assert.notEmpty(headers
                , "Parameter \"headers\" must not empty. ");
        for (Map.Entry<?, ?> entry : headers.entrySet()) {
            String key = entry.getKey() != null
                    ? entry.getKey().toString() : EMPTY_STRING;
            String val = entry.getValue() != null
                    ? entry.getValue().toString() : EMPTY_STRING;
            this.propertiesMapping.put(val, key);
            this.headersMapping.put(key, val);
        }
    }

    @Override
    public void removeHeaderByHeaderName(String headerName) {
        Assert.notNull(headerName
                , "Parameter \"headerName\" must not null. ");
        if (!headersMapping.containsKey(headerName)) { return; }
        String propertyName = this.headersMapping.get(headerName);
        this.propertiesMapping.remove(propertyName);
        this.headersMapping.remove(headerName);
    }

    @Override
    public void removeHeaderByPropertyName(String propertyName) {
        Assert.notNull(propertyName
                , "Parameter \"propertyName\" must not null. ");
        if (!propertiesMapping.containsKey(propertyName)) { return; }
        String headerName = this.propertiesMapping.get(propertyName);
        this.propertiesMapping.remove(propertyName);
        this.headersMapping.remove(headerName);
    }

    @Override
    public void clearHeaders() {
        this.headersMapping.clear();
        this.propertiesMapping.clear();
    }

    @Override
    public <T> List<T> toBeanList(Class<T> clazz) {
        Assert.notNull(clazz, "Parameter \"clazz\" must not null. ");
        List<Map<String, Object>> mapList = this.toMapList();
        return BeanUtils.mapToBeanInList(mapList, clazz);
    }

    @Override
    public <T> void fromBeanList(List<T> beanList) {
        Assert.notEmpty(beanList, "Parameter \"beanList\" must not empty. ");
        List<Map<String, Object>> mapList = BeanUtils.beanToMapInList(beanList);
        this.fromMapList(mapList);
    }

    @Override
    public List<Map<String, Object>> toMapList() {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        int lastRowNumber = this.getLastRowNumber();
        boolean haveHeaders = MapUtils.isNotEmpty(headersMapping);
        List<String> propertyList = new ArrayList<String>();
        boolean isFirst = true;
        for (int i = columnStartNumber; i <= lastRowNumber; i++) {
            List<Object> rowContent = this.getRowContent(i);
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
            Map<String, Object> map = new HashMap<String, Object>();
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
            this.workbook = null;
            this.createWorkbook(null);
        }
        catch (IOException e) {
            throw ExceptionUtils.wrap(e);
        }
        List<String> headerList = new ArrayList<String>();
        if (rowStartNumber != 0) {
            for (int i = 0; i < rowStartNumber; i++) {
                headerList.add(EMPTY_STRING);
            }
        }
        boolean haveHeaders = MapUtils.isNotEmpty(propertiesMapping);
        if (haveHeaders) {
            headerList.addAll(propertiesMapping.values());
        }
        else {
            Map<String, Object> first = CollectionUtils.getFirstNotNullElement(mapList);
            headerList.addAll(first.keySet());
        }
        if (columnStartNumber != 0) {
            for (int i = 0; i < columnStartNumber; i++) {
                this.setRowContent(i, new ArrayList<String>());
            }
        }
        int columnNumber = columnStartNumber;
        this.setRowContent(columnNumber, headerList);
        for (Map<String, Object> beanMap : mapList) {
            if (beanMap == null) { continue; }
            List<String> row = new ArrayList<String>();
            if (rowStartNumber != 0) {
                for (int i = 0; i < rowStartNumber; i++) {
                    row.add(EMPTY_STRING);
                }
            }
            if (haveHeaders) {
                for (String property : propertiesMapping.keySet()) {
                    Object val = beanMap.get(property);
                    row.add(val != null ? val.toString() : EMPTY_STRING);
                }
            }
            else {
                for (Object val : beanMap.values()) {
                    row.add(val != null ? val.toString() : EMPTY_STRING);
                }
            }
            this.setRowContent(++columnNumber, row);
        }
    }

}

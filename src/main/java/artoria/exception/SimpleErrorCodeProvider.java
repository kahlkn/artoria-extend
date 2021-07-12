package artoria.exception;

import artoria.common.SimpleErrorCode;
import artoria.file.Csv;
import artoria.util.MapUtils;
import artoria.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static artoria.common.Constants.DEFAULT_ENCODING_NAME;

public class SimpleErrorCodeProvider implements ErrorCodeProvider {
    private static final String DEFAULT_FILE_NAME = "error-code.csv";
    private final Map<String, ErrorCode> map = new ConcurrentHashMap<String, ErrorCode>();

    public SimpleErrorCodeProvider() {

        this(DEFAULT_FILE_NAME, DEFAULT_ENCODING_NAME);
    }

    public SimpleErrorCodeProvider(String fileInClasspath, String charset) {
        try {
            Csv csv = new Csv();
            csv.addHeader("code", "code");
            csv.addHeader("description", "description");
            csv.setCharset(charset);
            try {
                csv.readFromClasspath(fileInClasspath);
            }
            catch (IllegalArgumentException e) {
            }
            List<Map<String, Object>> mapList = csv.toMapList();
            for (Map<String, Object> objectMap : mapList) {
                if (MapUtils.isEmpty(objectMap)) { continue; }
                String description = (String) objectMap.get("description");
                String code = (String) objectMap.get("code");
                if (StringUtils.isBlank(description)) { continue; }
                if (StringUtils.isBlank(code)) { continue; }
                map.put(code, new SimpleErrorCode(code, description));
            }
        }
        catch (Exception e) {
            throw ExceptionUtils.wrap(e);
        }
    }

    @Override
    public ErrorCode getInstance(String errorCode) {
        if (StringUtils.isBlank(errorCode)) { return null; }
        return map.get(errorCode);
    }

}

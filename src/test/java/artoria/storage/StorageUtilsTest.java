package artoria.storage;

import artoria.logging.Logger;
import artoria.logging.LoggerFactory;
import com.alibaba.fastjson.JSON;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

@Ignore
public class StorageUtilsTest {
    private static Logger log = LoggerFactory.getLogger(StorageUtilsTest.class);
    private static String bucketName = "E:\\Test";

    static {
        StorageProvider storageProvider = StorageUtils.getStorageProvider("local");
        storageProvider.setDefaultBucketName("E:\\Test");
    }

    @Test
    public void test1() {
        File file = new File("E:\\test.png");
        Map<String, Object> metadata = new LinkedHashMap<String, Object>();
        metadata.put("test-metadata", "metadata");
        metadata.put("file-type", "png");
        StorageUtils.putObject(bucketName, "2019\\09\\15\\01\\20190915163400.png", file, metadata);
    }

    @Test
    public void test2() throws Exception {
        ListObjectsResult listObjectsResult = StorageUtils.listObjects("E:/Temp/", "/");
        System.out.println(JSON.toJSONString(listObjectsResult.getObjects(), true));
        StorageObject storageObject = StorageUtils.getObject(bucketName, "2019\\09\\15\\01\\20190915163400.png");
//        Map<String, Object> metadata = storageObject.getMetadata();
//        System.out.println(metadata);
//        InputStream objectContent = storageObject.getObjectContent();
//        FileUtils.write(objectContent, new File("E:\\test_" + System.currentTimeMillis() + ".png"));
//        CloseUtils.closeQuietly(objectContent);
    }

}

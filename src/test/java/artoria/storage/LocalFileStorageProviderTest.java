package artoria.storage;

import artoria.file.FileUtils;
import artoria.util.CloseUtils;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

@Ignore
public class LocalFileStorageProviderTest {
    private static StorageProvider storageProvider = new LocalFileStorageProvider();

    @Test
    public void test1() {
//        File file = new File("E:\\test.png");
//        Map<String, Object> metadata = new LinkedHashMap<String, Object>();
//        metadata.put("test.metadata", "metadata");
//        metadata.put("file.type", "png");
//        storageProvider.putObject("E:\\Test", "2019\\09\\15\\01\\20190915163400.png", file, metadata);
    }

    @Test
    public void test2() throws Exception {
//        StorageObject storageObject = storageProvider.getObject("E:\\Test", "2019\\09\\15\\01\\20190915163400.png");
//        Map<String, Object> metadata = storageObject.getMetadata();
//        System.out.println(metadata);
//        InputStream objectContent = storageObject.getObjectContent();
//        FileUtils.write(objectContent, new File("E:\\test_" + System.currentTimeMillis() + ".png"));
//        CloseUtils.closeQuietly(objectContent);
    }

}

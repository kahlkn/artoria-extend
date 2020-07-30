package artoria.util;

import artoria.codec.Hex;
import artoria.file.FileUtils;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;

@Ignore
public class FileTypeTest {
    private Hex hex = new Hex();

    @Test
    public void test1() throws Exception {
        System.out.println(FileType.check(new File("e:\\1.doc")));
        System.out.println(FileType.check(new File("e:\\1.xls")));
        System.out.println(FileType.check(new File("e:\\1.ppt")));
        System.out.println(FileType.check(new File("e:\\1.docx")));
        System.out.println(FileType.check(new File("e:\\1.xlsx")));
        System.out.println(FileType.check(new File("e:\\1.pptx")));
        System.out.println(FileType.check(new File("e:\\1.jar")));
    }

    @Test
    public void test2() throws Exception {
//        System.out.println(FileType.check(new File("e:\\1.exe")));
//        System.out.println(FileType.check(new File("e:\\1.rar")));
//        System.out.println(FileType.check(new File("e:\\1.zip")));
//        System.out.println(FileType.check(new File("e:\\1.rtf")));
//        System.out.println(FileType.check(new File("e:\\1.mp4")));
//        System.out.println(FileType.check(new File("e:\\1.class")));
        System.out.println(FileType.fileHeader(new File("e:\\1.class")).substring(0, 64));
    }

    @Test
    public void testClass() throws Exception {
        byte[] bytes = FileUtils.read(new File("e:\\1.class"));
        // class file top four is magic number, magic number value always is "CAFEBABE".
        String magic = hex.encodeToString(Arrays.copyOfRange(bytes, 0, 4));
        System.out.println("magic number: " + magic.toUpperCase());

        int minorVersion = (((int)bytes[4]) << 8) + bytes[5];
        int majorVersion = (((int)bytes[6]) << 8) + bytes[7];
        System.out.println(bytes[4] + " | " + bytes[5] + " | " + bytes[6] + " | " + bytes[7]);
        System.out.println("major version : " + majorVersion + ", minor version : " + minorVersion);
    }

}

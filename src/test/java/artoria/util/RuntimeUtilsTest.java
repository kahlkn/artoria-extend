package artoria.util;

import artoria.io.IOUtils;
import org.junit.Ignore;
import org.junit.Test;

import java.io.*;

@Ignore
public class RuntimeUtilsTest {

    @Test
    public void test() throws Exception {
        Process process = Runtime.getRuntime().exec("cmd");

        read(process.getInputStream(), System.out);
        read(process.getErrorStream(), System.err);

        OutputStream outputStream = process.getOutputStream();
        outputStream.write("ping -t www.github.com".getBytes());
        outputStream.flush();

        int exitCode = process.waitFor();

        if (exitCode == 0) {
            System.out.println("SUCCESS_MESSAGE");
        } else {
            System.err.println("ERROR_MESSAGE " + exitCode);
        }
    }

    private static void read(InputStream inputStream, PrintStream out) throws Exception {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "GBK"));

            String line;
            while ((line = reader.readLine()) != null) {
                out.println(line);
            }
        }
        finally {
            CloseUtils.closeQuietly(inputStream);
        }
    }

    @Test
    public void test1() throws IOException {
        Runtime runtime = Runtime.getRuntime();
        Process exec = runtime.exec("netstat -ano");
        InputStream in = exec.getInputStream();
        System.out.println(IOUtils.toString(in, "GB2312"));
    }

    @Test
    public void test2() throws IOException {
//        String cmd = "ping www.baidu.com";
        String cmd = "netstat -ano";
        String charset = "GB2312";
        Process process = RuntimeUtils.exec(cmd);
        System.out.println(RuntimeUtils.run(process, charset));
    }

    @Test
    public void test2_1() throws IOException {
        Process process = RuntimeUtils.exec(new String[]{"ping","www.baidu.com"});
        System.out.println(RuntimeUtils.run(process, "GB2312"));
    }

    @Test
    public void test3() throws IOException {
        String cmd = "ping -t www.baidu.com";
        String charset = "GB2312";
        Process process = RuntimeUtils.exec(cmd);
        System.out.println(RuntimeUtils.run(process, 1000L, charset));
    }

    @Test
    public void test4() throws IOException {
        String cmd = "netstat -ano";
        String charset = "GB2312";
        System.out.println(RuntimeUtils.run(cmd, charset));
    }

    @Test
    public void test5() throws IOException {
        String cmd = "ping -t www.baidu.com";
        String charset = "GB2312";
        System.out.println(RuntimeUtils.run(cmd, 1000L, charset));
    }

}

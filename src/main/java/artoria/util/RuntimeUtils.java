package artoria.util;

import artoria.io.IOUtils;

import java.io.*;

import static artoria.common.Constants.DEFAULT_CHARSET_NAME;
import static artoria.common.Constants.NEWLINE;

/**
 * @author Kahle
 */
@Deprecated
public class RuntimeUtils {
    private static final Runtime runtime = Runtime.getRuntime();

    public static Process exec(String command) throws IOException {
        return runtime.exec(command);
    }

    public static Process exec(String[] cmdarray) throws IOException {
        return runtime.exec(cmdarray);
    }

    public static Process exec(String command, String[] envp) throws IOException {
        return runtime.exec(command, envp);
    }

    public static Process exec(String[] cmdarray, String[] envp) throws IOException {
        return runtime.exec(cmdarray, envp);
    }

    public static Process exec(String command, String[] envp, File dir) throws IOException {
        return runtime.exec(command, envp, dir);
    }

    public static Process exec(String[] cmdarray, String[] envp, File dir) throws IOException {
        return runtime.exec(cmdarray, envp, dir);
    }

    public static String run(Process process)
            throws IOException {
        return run(process, DEFAULT_CHARSET_NAME);
    }

    public static String run(Process process, String encoding)
            throws IOException {
        InputStream in = null;
        try {
            in = process.getInputStream();
            return IOUtils.toString(in, encoding);
        }
        finally {
            CloseUtils.closeQuietly(in);
            process.destroy();
        }
    }

    public static String run(Process process, long runtime)
            throws IOException {
        return run(process, runtime, DEFAULT_CHARSET_NAME);
    }

    public static String run(Process process, long runtime, String encoding)
            throws IOException {
        BufferedReader reader = null;
        try {
            InputStream i = process.getInputStream();
            InputStreamReader r = new InputStreamReader(i, encoding);
            reader = new BufferedReader(r);

            StringBuilder builder = new StringBuilder();
            long target = System.currentTimeMillis() + runtime;
            long current = System.currentTimeMillis();
            for (String line; (line = reader.readLine()) != null
                    && current < target; ) {
                builder.append(line).append(NEWLINE);
                current = System.currentTimeMillis();
            }
            return builder.toString();
        }
        finally {
            CloseUtils.closeQuietly(reader);
            process.destroy();
        }
    }

    public static String run(String command)
            throws IOException {
        return run(command, DEFAULT_CHARSET_NAME);
    }

    public static String run(String command, String encoding)
            throws IOException {
        return run(exec(command), encoding);
    }

    public static String run(String command, long runtime)
            throws IOException {
        return run(command, runtime, DEFAULT_CHARSET_NAME);
    }

    public static String run(String command, long runtime, String encoding)
            throws IOException {
        return run(exec(command), runtime, encoding);
    }

}

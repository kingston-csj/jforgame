package jforgame.hotswap;

import com.sun.tools.attach.VirtualMachine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JavaDoctor {

    private static final JavaDoctor self = new JavaDoctor();

    public static byte[] fixData;

    public static String log;

    public static Exception exception;

    private Logger logger = LoggerFactory.getLogger(JavaDoctor.class.getName());

    public static JavaDoctor getInstance() {
        return self;
    }

    public synchronized boolean hotSwap(String filePath) throws Exception {
        List<File> files = FileUtil.listFiles(filePath);
        ByteArrayOutputStream bou = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(bou));

        Map<String, byte[]> classBytes = new HashMap<>();
        for (File file : files) {
            if (file.getName().endsWith(".class")) {
                ClassFileMeta fileMeta = new ClassFileMeta(file);
                    FileInputStream fis =  new FileInputStream(file);
                    byte[] bytes = new byte[fis.available()];
                    fis.read(bytes);
                    classBytes.put(fileMeta.className, bytes);
            }
        }

        DynamicClassLoader classLoader = new DynamicClassLoader(classBytes);
        for (Map.Entry<String, byte[]> entry : classBytes.entrySet()) {
                classLoader.loadClass(entry.getKey());
        }

        dos.writeInt(classBytes.entrySet().size());
        for (Map.Entry<String, byte[]> entry : classBytes.entrySet()) {
            String fileName = entry.getKey();
            dos.writeUTF(fileName);
            dos.writeInt(entry.getValue().length);
            dos.write(entry.getValue());
        }
        dos.flush();

        fixData = bou.toByteArray();

        reloadClass(filePath, classBytes);

        return true;
    }

    private String reloadClass(String path,  Map<String, byte[]> classBytes) {
        try {
            // 拿到当前jvm的进程id
            String pid = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
            VirtualMachine vm = VirtualMachine.attach(pid);
            log = "empty";
            exception = null;
            logger.error("hot swap directory [{}]，total {} files", path, classBytes.size());
            vm.loadAgent("agent/hotswap-agent.jar");
            logger.error("hot swap finished --> {}", log);
            if (exception != null) {
                logger.error("hot swap failed {}", exception);
            }
            return log;
        } catch (Throwable e) {
            logger.error("", e);
            return "hot swap failed";
        }
    }
}

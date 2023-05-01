package jforgame.hotswap;

import com.sun.tools.attach.VirtualMachine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JavaDoctor {

    private static final JavaDoctor self = new JavaDoctor();

    private Map<String, ClassFileMeta> reloadFiles = new HashMap<>();

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
        DynamicClassLoader classLoader = new DynamicClassLoader();
        Map<String, ClassFileMeta> currFiles = new HashMap<>();
        for (File file : files) {
            if (file.getName().endsWith(".class")) {
                ClassFileMeta fileMeta = new ClassFileMeta(file);
                reloadFiles.put(fileMeta.className, fileMeta);
                currFiles.put(fileMeta.className, fileMeta);
                classLoader.loadClass(fileMeta.className);
            }
        }

        dos.writeInt(currFiles.entrySet().size());
        for (Map.Entry<String, ClassFileMeta> entry : currFiles.entrySet()) {
            String fileName = entry.getKey();
            ClassFileMeta meta = entry.getValue();
            dos.writeUTF(fileName);
            dos.writeInt(meta.data.length);
            dos.write(meta.data);
        }
        dos.flush();

        fixData = bou.toByteArray();

        reloadClass(filePath, currFiles);

        return true;
    }

    private String reloadClass(String path, Map<String, ClassFileMeta> reloadFiles) {
        try {
            // 拿到当前jvm的进程id
            String pid = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
            VirtualMachine vm = VirtualMachine.attach(pid);
            log = "empty";
            exception = null;
            logger.error("hot swap directory [{}]，total {} files", path, reloadFiles.size());
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

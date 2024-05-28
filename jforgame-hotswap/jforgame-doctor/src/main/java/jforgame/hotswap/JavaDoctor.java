package jforgame.hotswap;

import com.sun.tools.attach.VirtualMachine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.Map;

public class JavaDoctor {

    private static final Logger logger = LoggerFactory.getLogger(JavaDoctor.class.getName());

    public static byte[] fixData;

    public static String log;

    public static Exception exception;

    private static String agentPath = "agent" + File.separator + "jforgame-hotswap-agent.jar";

    public static synchronized boolean hotSwap(String filePath) throws Exception {
        DynamicClassLoader classLoader = new DynamicClassLoader(filePath);
        Map<String, byte[]> classBytes = classLoader.getClassBytes();
        for (Map.Entry<String, byte[]> entry : classBytes.entrySet()) {
            classLoader.loadClass(entry.getKey());
        }

        ByteArrayOutputStream bou = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(bou));
        dos.writeInt(classBytes.entrySet().size());
        for (Map.Entry<String, byte[]> entry : classBytes.entrySet()) {
            String fileName = entry.getKey();
            dos.writeUTF(fileName);
            dos.writeInt(entry.getValue().length);
            dos.write(entry.getValue());
        }
        dos.flush();

        fixData = bou.toByteArray();

        redefineClasses(filePath, classBytes);

        return true;
    }

    private static void redefineClasses(String path, Map<String, byte[]> classBytes) {
        try {
            // 拿到当前jvm的进程id
            String pid = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
            VirtualMachine vm = VirtualMachine.attach(pid);
            log = "empty";
            exception = null;
            logger.error("hot swap directory [{}]，total {} files", path, classBytes.size());
            vm.loadAgent(agentPath);
            logger.error("hot swap finished --> {}", log);
            if (exception != null) {
                logger.error("hot swap failed ", exception);
            }
        } catch (Throwable e) {
            logger.error("", e);
        }
    }

    public static void setAgentPath(String agentPath) {
        JavaDoctor.agentPath = agentPath;
    }
}

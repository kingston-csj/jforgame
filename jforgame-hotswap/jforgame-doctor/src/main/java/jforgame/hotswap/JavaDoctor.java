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
            // 这里使用loadClass, 严格遵循双类委派机制
            // 对于已经加载的类，不重复加载
            // 只加载新的类
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

        return redefineClasses(filePath, classBytes);
    }

    private static boolean redefineClasses(String path, Map<String, byte[]> classBytes) {
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
                return false;
            }
            return true;
        } catch (Throwable e) {
            logger.error("", e);
            return false;
        }
    }

    public static void setAgentPath(String agentPath) {
        JavaDoctor.agentPath = agentPath;
    }
}

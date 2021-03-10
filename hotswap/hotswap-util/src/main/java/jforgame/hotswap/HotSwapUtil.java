package jforgame.hotswap;

import com.sun.tools.attach.VirtualMachine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HotSwapUtil {

    public static volatile Exception exception;

    public static volatile String log;

    private static Logger logger = LoggerFactory.getLogger(HotSwapUtil.class);

    public static String reloadClass(String path) {
        try {
            // 拿到当前jvm的进程id
            String pid = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
            VirtualMachine vm = VirtualMachine.attach(pid);
            path = "hotswap" + File.separator + path;
            log = "empty";
            exception = null;
            List<File> files = listFiles(path);
            logger.error("热更目录[{}]，总共有{}个文件", path, files.size());
            for (File file : files) {
                String classPath = path + File.separator + file.getName();
                logger.error("reload path ==" + classPath);
                // path参数即agentmain()方法的第一个参数
                vm.loadAgent("agent/hotswap-agent.jar", classPath);
                logger.error("热更日志{}", log);
                logger.error("热更异常{}", exception);
            }
//            return "热更的文件有 " + String.join(",", succFiles);
            return log;
        } catch (Throwable e) {
            logger.error("", e);
            return "热更失败";
        }
    }

    private static List<File> listFiles(String path) {
        List<File> result = new ArrayList<>();
        try {
            File file = new File(path);
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                result.addAll(Arrays.asList(files));
            } else {
                result.add(file);
            }
        } catch (Exception e) {

        }
        return result;
    }
}

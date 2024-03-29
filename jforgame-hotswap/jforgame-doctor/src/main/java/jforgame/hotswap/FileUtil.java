package jforgame.hotswap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class FileUtil {

    static Logger logger = LoggerFactory.getLogger(FileUtil.class.getName());

    static List<File> listFiles(String path) {
        List<File> result = new ArrayList<>();
        try {
            File file = new File(path);
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                assert files != null;
                result.addAll(Arrays.asList(files));
            } else {
                result.add(file);
            }
        } catch (Exception ignore) {

        }
        return result;
    }

    static Map<String, byte[]> readClassData(String filePath) {
        List<File> files = FileUtil.listFiles(filePath);
        Map<String, byte[]> classBytes = new HashMap<>();
        for (File file : files) {
            if (file.getName().endsWith(".class")) {
                try {
                    ClassFileMeta fileMeta = new ClassFileMeta(file);
                    try (FileInputStream fis = new FileInputStream(file)) {
                        byte[] bytes = new byte[fis.available()];
                        fis.read(bytes);
                        classBytes.put(fileMeta.className, bytes);
                    }
                } catch (Exception e) {
                    logger.error("", e);
                }
            }
        }
        return classBytes;
    }

}

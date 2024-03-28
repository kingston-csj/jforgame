package jforgame.hotswap;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileUtil {

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
        } catch (Exception ignored) {

        }
        return result;
    }

}

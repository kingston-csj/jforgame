package jforgame.hotswap;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ExecStatistics {

    private long usedTime;

    private static List<File> succ = new ArrayList<>();

    private static List<File> failed = new ArrayList<>();

    public void addSucc(File file) {
        this.succ.add(file);
    }

    public void addFailed(File file) {
        this.failed.add(file);
    }

    @Override
    public String toString() {
        if (failed.size() == 0) {
            return succ.size() + "个文件全部执行成功";
        }
        StringBuilder sb = new StringBuilder("执行成功的文件：");
        succ.forEach((f) -> {
            sb.append(f.getName()).append(",");
        });
        sb.append("\n执行失败的文件：");
        failed.forEach((f) -> {
            sb.append(f.getName()).append(",");
        });
        return sb.toString();
    }

}

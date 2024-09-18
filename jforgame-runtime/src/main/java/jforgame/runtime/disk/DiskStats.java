package jforgame.runtime.disk;

import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DiskStats {

    private static final oshi.SystemInfo systemInfo = new oshi.SystemInfo();

    private static final OperatingSystem os = systemInfo.getOperatingSystem();

    /**
     * 对应于Linux系统的df -h命令，兼容windows
     */
    public static List<DiskFileSystemVo> df() {
        List<OSFileStore> fileStores = os.getFileSystem().getFileStores();
        List<DiskFileSystemVo> dfs = new ArrayList<>();
        for (OSFileStore fs : fileStores) {
            String name = fs.getName();
            long size = fs.getTotalSpace();
            long available = fs.getFreeSpace();
            dfs.add(DiskFileSystemVo.valueOf(name, size, available).toGB());
        }
        Collections.sort(dfs);
        return dfs;
    }

}

package jforgame.runtime.disk;

import jforgame.runtime.util.IoUtil;

public class DiskFileSystemVo implements Comparable<DiskFileSystemVo> {

    private String name;

    private long size;

    private long available;

    private String unit;

    static final String UNIT_BYTE = "B";
    static final String UNIT_K_BYTE = "KB";
    static final String UNIT_M_BYTE = "MB";
    static final String UNIT_G_BYTE = "GB";

    public static DiskFileSystemVo valueOf(String name, long size, long available) {
        DiskFileSystemVo dfs = new DiskFileSystemVo();
        dfs.name = name;
        dfs.size = size;
        dfs.available = available;
        dfs.unit = UNIT_BYTE;
        return dfs;
    }

    public double usage() {
        return ((double) (size - available)) / size;
    }

    @Override
    public int compareTo(DiskFileSystemVo other) {
        if (other == null) {
            return 1;
        }

        return Double.compare(usage(), other.usage());
    }

    public DiskFileSystemVo toMB() {
        long size = this.size / IoUtil.BYTES_PER_MB;
        long available = this.available / IoUtil.BYTES_PER_MB;
        DiskFileSystemVo dfs = DiskFileSystemVo.valueOf(this.name, size, available);
        dfs.unit = UNIT_M_BYTE;
        return dfs;
    }

    public DiskFileSystemVo toGB() {
        long size = (long) Math.ceil(this.size / (double) IoUtil.BYTES_PER_GB);
        long available = (long) Math.ceil(this.available / (double) IoUtil.BYTES_PER_GB);
        DiskFileSystemVo dfs = DiskFileSystemVo.valueOf(this.name, size, available);
        dfs.unit = UNIT_G_BYTE;
        return dfs;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getAvailable() {
        return available;
    }

    public void setAvailable(long available) {
        this.available = available;
    }

    @Override
    public String toString() {
        return "DiskFileSystem{" +
                "name='" + name + '\'' +
                ", size=" + size + "(" + unit + ")"+
                ", available=" + available + "(" + unit+ ")" +
                '}';
    }

}

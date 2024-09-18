package jforgame.runtime;

import jforgame.runtime.disk.DiskFileSystemVo;
import jforgame.runtime.disk.DiskStats;

import java.util.List;

public class TestDisk {

    public static void main(String[] args) {
        List<DiskFileSystemVo> df = DiskStats.df();
        df.forEach(System.out::println);
    }
}

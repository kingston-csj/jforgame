package jforgame.runtime.os;

import oshi.hardware.CentralProcessor;
import oshi.hardware.HardwareAbstractionLayer;

public class OsStats {

    private static final oshi.SystemInfo systemInfo = new oshi.SystemInfo();

    private static final HardwareAbstractionLayer hardware = systemInfo.getHardware();

    private static long[] ticks = hardware.getProcessor().getSystemCpuLoadTicks();

    /**
     * 对应于Linux系统的uptime命令，windows统一返回-1.0
     */
    public static UptimeVo uptime() {
        CentralProcessor processor = hardware.getProcessor();
        double[] loads = processor.getSystemLoadAverage(3);
        double oneMinute = loads[0];
        double fiveMinute = loads[1];
        double fiftyMinute = loads[2];

        long[] cpuTicks = processor.getSystemCpuLoadTicks();
        double usage = processor.getSystemCpuLoadBetweenTicks(ticks);

        ticks = cpuTicks;
        return UptimeVo.valueOf(oneMinute, fiveMinute, fiftyMinute, usage, System.currentTimeMillis());
    }

}

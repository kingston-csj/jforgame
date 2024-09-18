package jforgame.runtime.os;

public class UptimeVo implements Comparable<UptimeVo> {

    private double oneMinute;

    private double fiveMinute;

    private double fiftyMinute;

    private double usage;

    private long time;

    public static UptimeVo valueOf(double oneMinute, double fiveMinute, double fiftyMinute, double usage, long time) {
        UptimeVo uptime = new UptimeVo();
        uptime.oneMinute = oneMinute;
        uptime.fiveMinute = fiveMinute;
        uptime.fiftyMinute = fiftyMinute;
        uptime.usage = usage;
        uptime.time = time;
        return uptime;
    }

    @Override
    public int compareTo(UptimeVo target) {
        if (target == null) {
            return 1;
        }

        return Double.compare(this.usage, target.getUsage());
    }

    public double getOneMinute() {
        return oneMinute;
    }

    public double getFiveMinute() {
        return fiveMinute;
    }

    public double getFiftyMinute() {
        return fiftyMinute;
    }

    public double getUsage() {
        return usage;
    }

    public long getTime() {
        return time;
    }

    @Override
    public String toString() {
        return "Uptime{" +
                "oneMinute=" + oneMinute +
                ", fiveMinute=" + fiveMinute +
                ", fiftyMinute=" + fiftyMinute +
                ", usage=" + usage +
                ", timestamp=" + time +
                '}';
    }
}

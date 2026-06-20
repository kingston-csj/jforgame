package jforgame.socket.core.monitoring;

/**
 * Default traffic observer, internally reuses {@link TrafficStatistic} for statistics.
 */
public class DefaultTrafficObserver implements MessageTrafficObserver {

    public static final DefaultTrafficObserver INSTANCE = new DefaultTrafficObserver();

    @Override
    public void onInbound(int cmd, int bytes) {
        TrafficStatistic.addReceivedBytes(cmd, bytes);
        TrafficStatistic.addReceivedNumber(cmd);
    }

    @Override
    public void onOutbound(int cmd, int bytes) {
        TrafficStatistic.addSentBytes(cmd, bytes);
        TrafficStatistic.addSentNumber(cmd);
    }
}

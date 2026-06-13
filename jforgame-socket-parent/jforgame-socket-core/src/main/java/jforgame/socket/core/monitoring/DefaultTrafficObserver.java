package jforgame.socket.core.monitoring;

/**
 * 默认流量观测器，内部仍然复用 {@link TrafficStatistic} 完成统计。
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

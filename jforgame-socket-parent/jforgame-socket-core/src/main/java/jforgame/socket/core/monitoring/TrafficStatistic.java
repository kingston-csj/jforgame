package jforgame.socket.core.monitoring;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Traffic statistics, counts input/output byte count and message count
 */
public class TrafficStatistic {

    /**
     * input <messageId, totalBytesLength>
     */
    private static ConcurrentMap<Integer, AtomicLong> receivedBytes = new ConcurrentHashMap<>();

    /**
     * output <messageId, totalBytesLength>
     */
    private static ConcurrentMap<Integer, AtomicLong> sentBytes = new ConcurrentHashMap<>();


    /**
     * input <messageId, totalCount>
     */
    private static ConcurrentMap<Integer, AtomicInteger> receivedNumbers = new ConcurrentHashMap<>();

    /**
     * output <messageId, totalCount>
     */
    private static ConcurrentMap<Integer, AtomicInteger> sentNumbers = new ConcurrentHashMap<>();

    /**
     * Add received bytes
     *
     * @param cmd     message id
     * @param msgLength message length
     */
    public static void addReceivedBytes(int cmd, int msgLength) {
        receivedBytes.putIfAbsent(cmd, new AtomicLong());
        receivedBytes.get(cmd).getAndAdd(msgLength);
    }

    /**
     * Add sent bytes
     *
     * @param cmd     message id
     * @param msgLength message length
     */
    public static void addSentBytes(int cmd, int msgLength) {
        sentBytes.putIfAbsent(cmd, new AtomicLong());
        sentBytes.get(cmd).getAndAdd(msgLength);
    }

    /**
     * Add received message count
     *
     * @param cmd message id
     */
    public static void addReceivedNumber(int cmd) {
        receivedNumbers.putIfAbsent(cmd, new AtomicInteger());
        receivedNumbers.get(cmd).getAndIncrement();
    }

    /**
     * Add sent message count
     *
     * @param cmd message id
     */
    public static void addSentNumber(int cmd) {
        sentNumbers.putIfAbsent(cmd, new AtomicInteger());
        sentNumbers.get(cmd).getAndIncrement();
    }

    /**
     * Reset received bytes and message count
     */
    public static void resetReceivedBytes() {
        receivedBytes.clear();
        receivedNumbers.clear();
    }

    /**
     * Reset sent bytes and message count
     */
    public static void resetSentBytes() {
        sentBytes.clear();
        sentNumbers.clear();
    }

    /**
     * Show received bytes
     *
     * @return &lt;messageId, bytes&gt;
     */
    public static Map<Integer, Long> showReceivedBytes() {
        return receivedBytes.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, v -> v.getValue().get()));
    }

    /**
     * Show sent bytes
     *
     * @return &lt;messageId, bytes&gt;
     */
    public static Map<Integer, Long> showSentBytes() {
        return sentBytes.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, v -> v.getValue().get()));
    }

    /**
     * Show received message count
     * @return &lt;messageId, count&gt;
     */
    public static Map<Integer, Integer> showReceivedNumbers() {
        return receivedNumbers.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, v -> v.getValue().get()));
    }

    /**
     * Show sent message count
     * @return &lt;messageId, count&gt;
     */
    public static Map<Integer, Integer> showSentNumbers() {
        return sentNumbers.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, v -> v.getValue().get()));
    }

}

package jforgame.socket.share;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * 流量统计，统计输入，输出流的字节数量，消息数量
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
     * input <messageId, totalBytesLength>
     */
    private static ConcurrentMap<Integer, AtomicInteger> receivedNumbers = new ConcurrentHashMap<>();

    /**
     * output <messageId, totalBytesLength>
     */
    private static ConcurrentMap<Integer, AtomicInteger> sentNumbers = new ConcurrentHashMap<>();

    /**
     * 添加收到的字节数
     *
     * @param cmd     消息id
     * @param msgLength 消息长度
     */
    public static void addReceivedBytes(int cmd, int msgLength) {
        receivedBytes.putIfAbsent(cmd, new AtomicLong());
        receivedBytes.get(cmd).getAndAdd(msgLength);
    }

    /**
     * 添加发送的字节数
     *
     * @param cmd     消息id
     * @param msgLength 消息长度
     */
    public static void addSentBytes(int cmd, int msgLength) {
        sentBytes.putIfAbsent(cmd, new AtomicLong());
        sentBytes.get(cmd).getAndAdd(msgLength);
    }

    /**
     * 添加收到的消息数
     *
     * @param cmd 消息id
     */
    public static void addReceivedNumber(int cmd) {
        receivedNumbers.putIfAbsent(cmd, new AtomicInteger());
        receivedNumbers.get(cmd).getAndIncrement();
    }

    /**
     * 添加发送的消息数
     *
     * @param cmd 消息id
     */
    public static void addSentNumber(int cmd) {
        sentNumbers.putIfAbsent(cmd, new AtomicInteger());
        sentNumbers.get(cmd).getAndIncrement();
    }

    /**
     * 重置收到的字节数和消息数
     */
    public static void resetReceivedBytes() {
        receivedBytes.clear();
        receivedNumbers.clear();
    }

    /**
     * 重置发送的字节数和消息数
     */
    public static void resetSentBytes() {
        sentBytes.clear();
        sentNumbers.clear();
    }

    /**
     * 展示已接收的字节数
     *
     * @return &lt;消息id, 字节数&gt;
     */
    public static Map<Integer, Long> showReceivedBytes() {
        return receivedBytes.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, v -> v.getValue().get()));
    }

    /**
     * 显示已发送的字节数
     *
     * @return &lt;消息id, 字节数&gt;
     */
    public static Map<Integer, Long> showSentBytes() {
        return sentBytes.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, v -> v.getValue().get()));
    }

    /**
     * 显示已接收的消息数
     * @return &lt;消息id, 消息数&gt;
     */
    public static Map<Integer, Integer> showReceivedNumbers() {
        return receivedNumbers.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, v -> v.getValue().get()));
    }

    /**
     * 显示已发送的消息数
     * @return &lt;消息id, 消息数&gt;
     */
    public static Map<Integer, Integer> showSentNumbers() {
        return sentNumbers.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, v -> v.getValue().get()));
    }

}

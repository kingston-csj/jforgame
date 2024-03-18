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


    public static void addReceivedBytes(int cmd, int msgLength) {
        receivedBytes.putIfAbsent(cmd, new AtomicLong());
        receivedBytes.get(cmd).getAndAdd(msgLength);
    }

    public static void addSentBytes(int cmd, int msgLength) {
        sentBytes.putIfAbsent(cmd, new AtomicLong());
        sentBytes.get(cmd).getAndAdd(msgLength);
    }


    public static void addReceivedNumber(int cmd) {
        receivedNumbers.putIfAbsent(cmd, new AtomicInteger());
        receivedNumbers.get(cmd).getAndIncrement();
    }

    public static void addSentNumber(int cmd) {
        sentNumbers.putIfAbsent(cmd, new AtomicInteger());
        sentNumbers.get(cmd).getAndIncrement();
    }


    public static void resetReceivedBytes() {
        receivedBytes.clear();
        receivedNumbers.clear();
    }


    public static void resetSentBytes() {
        sentBytes.clear();
        sentNumbers.clear();
    }


    public static Map<Integer, Long> showReceivedBytes() {
        return receivedBytes.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, v -> v.getValue().get()));
    }

    public static Map<Integer, Long> showSentBytes() {
        return sentBytes.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, v -> v.getValue().get()));
    }

    public static Map<Integer, Integer> showReceivedNumbers() {
        return receivedNumbers.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, v -> v.getValue().get()));
    }

    public static Map<Integer, Integer> showSentNumbers() {
        return sentNumbers.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, v -> v.getValue().get()));
    }



}

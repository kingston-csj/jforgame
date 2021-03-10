package jforgame.server.net;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public class MessageStatistics {

    private static MessageStatistics self = new MessageStatistics();

    private ConcurrentMap<Integer, AtomicInteger> sentCounter = new ConcurrentHashMap<>();

    private ConcurrentMap<Integer, AtomicInteger> receivedCounter = new ConcurrentHashMap<>();

    public static MessageStatistics getInstance() {
        return self;
    }

    public void addReceived(int cmd) {
        receivedCounter.putIfAbsent(cmd, new AtomicInteger());
        AtomicInteger counter = receivedCounter.get(cmd);
        counter.incrementAndGet();
    }

    public void addSent(int cmd) {
        sentCounter.putIfAbsent(cmd, new AtomicInteger());
        AtomicInteger counter = sentCounter.get(cmd);
        counter.incrementAndGet();
    }

    public void reset() {
        sentCounter.clear();
        receivedCounter.clear();
    }

    @Override
    public String toString() {
        List<CmdRecord> received = new ArrayList<>();
        List<CmdRecord> sent = new ArrayList<>();

        for (Map.Entry<Integer, AtomicInteger> entry : receivedCounter.entrySet()) {
            received.add(new CmdRecord(entry.getKey(), entry.getValue().get()));
        }

        for (Map.Entry<Integer, AtomicInteger> entry : sentCounter.entrySet()) {
            sent.add(new CmdRecord(entry.getKey(), entry.getValue().get()));
        }

        Collections.sort(received);
        Collections.sort(sent);

        StringBuilder result = new StringBuilder();

        result.append("received = ").append(received.toString()).append("\n")
                .append("sent = ").append(sent.toString());

        return result.toString();
    }
}

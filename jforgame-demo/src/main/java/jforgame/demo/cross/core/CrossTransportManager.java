package jforgame.demo.cross.core;

import jforgame.commons.thread.NamedThreadFactory;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class CrossTransportManager {

    private static volatile CrossTransportManager instance;

    private int defaultCoreSum = Runtime.getRuntime().availableProcessors();

    private ExecutorService[] services;

    private C2SSessionPoolFactory sessionFactory;

    private AtomicInteger idFactory = new AtomicInteger();

    public static CrossTransportManager getInstance() {
        if (instance != null) {
            return instance;
        }
        synchronized (CrossTransportManager.class) {
            if (instance == null) {
                CrossTransportManager obj = new CrossTransportManager();
                obj.init();
                instance = obj;
            }

        }
        return instance;
    }

    private void init() {
        services = new ExecutorService[defaultCoreSum];
        for (int i = 0; i < defaultCoreSum; i++) {
            services[i] = Executors.newSingleThreadExecutor(new NamedThreadFactory("cross-ladder-transport" + i));
        }
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMaxTotal(5);
        config.setMaxWaitMillis(5000);
        sessionFactory = new C2SSessionPoolFactory(config);
    }

    /**
     * 同步发消息
     *
     * @param ip
     * @param port
     * @param message
     */
    public void sendMessage(String ip, int port, Object message) {
        NSessionPlus session = sessionFactory.borrowSession(ip, port);
        session.send(message);
    }

    /**
     * 异步发消息
     *
     * @param ip
     * @param port
     * @param message
     */
    public void sendMessageAsync(String ip, int port, Object message) {
        String key = ip + port;
        int index = key.hashCode() % defaultCoreSum;
        services[index].submit(() -> {
            sendMessage(ip, port, message);
        });
    }

}

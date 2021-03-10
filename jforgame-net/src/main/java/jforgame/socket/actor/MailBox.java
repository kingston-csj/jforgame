package jforgame.socket.actor;

import com.google.common.util.concurrent.AtomicLongMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * actor模型里的邮箱
 */
public class MailBox implements Runnable {

    private Logger logger = LoggerFactory.getLogger(MailBox.class);

    private AtomicLongMap<String> MODULE_QUEUE_FACTORY = AtomicLongMap.create();
    /**
     * 任务堆积警戒值
     */
    static int THRESHOLD = 100;

    private AtomicBoolean running = new AtomicBoolean(true);

    private long id;

    private String name;

    private Queue<Runnable> mails = new LinkedBlockingQueue();
    /**
     * 上一级任务总线
     */
    private LinkedBlockingQueue<Runnable> parent;
    /**
     * 当前任务是否在parent里
     */
    private AtomicBoolean queued = new AtomicBoolean(false);

    public MailBox(LinkedBlockingQueue<Runnable> parent, String module) {
        this.parent = parent;
        this.id = MODULE_QUEUE_FACTORY.getAndIncrement(module);
        this.name = module + "-" + id;
    }

    public void receive(Runnable mail) {
        if (!running.get()) {
            return;
        }
        this.mails.add(mail);
        if (queued.compareAndSet(false, true)) {
            parent.add(this);
        }
    }

    @Override
    public void run() {
        // 防止任务一直占线
        int size = mails.size();
        if (size > THRESHOLD) {
            logger.warn("[{}]任务堆积严重，任务数量[{}]", name, size);
        }
        try {
            while (!mails.isEmpty() && size-- >= 0) {
                Runnable task = mails.poll();
                task.run();
            }
        } catch (Exception e) {
            logger.error("", e);
        } finally {
            queued.compareAndSet(true, false);
        }
    }

    public void shutDown() {
        running.compareAndSet(true, false);
    }

}
package jforgame.threadmodel.actor.mailbox;

import jforgame.threadmodel.actor.mail.Mail;
import jforgame.threadmodel.actor.config.MailboxConfig;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * 有界邮箱实现
 * @author wupeng0528
 */
public class BoundedMailbox extends Mailbox {
    
    private final MailboxConfig config;
    
    public BoundedMailbox(MailboxConfig config) {
        super(createBoundedQueue(config.getCapacity()));
        this.config = config;
    }
    
    private static BlockingQueue<Mail> createBoundedQueue(int capacity) {
        return new ArrayBlockingQueue<>(capacity > 0 ? capacity : 512);
    }
    
    @Override
    public void receive(Mail mail) {
        if (!this.mails.offer(mail)) {
            // 当队列满时，根据配置决定是丢弃还是阻塞
            handleQueueFull(mail);
        }
    }
    
    /**
     * 处理队列满的情况(预留)
     */
    private void handleQueueFull(Mail mail) {
        // 可以根据配置决定策略：
        // 1. 丢弃新消息
        // 2. 丢弃旧消息
        // 3. 阻塞等待
        // 4. 抛出异常
        
        throw new IllegalStateException("BoundedMailbox queue is full, capacity: " + config.getCapacity());
    }
    
    public MailboxConfig getConfig() {
        return config;
    }
}

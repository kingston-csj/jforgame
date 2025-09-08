package jforgame.threadmodel.actor;

import java.util.concurrent.*;

/**
 * 邮箱工厂类
 * 根据配置创建不同类型的邮箱
 */
public class MailboxFactory {
    
    /**
     * 默认邮箱容量
     */
    private static final int DEFAULT_CAPACITY = 512;
    
    /**
     * 创建邮箱
     */
    public static Mailbox create(MailboxType type) {
        return create(type, DEFAULT_CAPACITY);
    }
    
    /**
     * 创建指定容量的邮箱
     */
    public static Mailbox create(MailboxType type, int capacity) {
        BlockingQueue<Mail> queue;
        
        switch (type) {
            case UNBOUNDED:
                queue = new LinkedBlockingQueue<>();
                break;
            case BOUNDED:
                queue = new ArrayBlockingQueue<>(capacity);
                break;
            case PRIORITY:
                queue = new PriorityBlockingQueue<>(capacity, MailboxFactory::compareMails);
                break;
            case UNBOUNDED_PRIORITY:
                queue = new PriorityBlockingQueue<>(DEFAULT_CAPACITY, MailboxFactory::compareMails);
                break;
            default:
                queue = new ArrayBlockingQueue<>(capacity);
                break;
        }
        
        return new Mailbox(queue);
    }
    
    /**
     * 邮件优先级比较器
     * 子类可以重写Mail的getPriority方法来自定义优先级
     */
    private static int compareMails(Mail m1, Mail m2) {
        int p1 = m1 instanceof PriorityMail ? ((PriorityMail) m1).getPriority() : 0;
        int p2 = m2 instanceof PriorityMail ? ((PriorityMail) m2).getPriority() : 0;
        return Integer.compare(p2, p1); // 高优先级在前
    }
}
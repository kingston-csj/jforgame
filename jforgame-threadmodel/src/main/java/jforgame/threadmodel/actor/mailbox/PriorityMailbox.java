package jforgame.threadmodel.actor.mailbox;

import jforgame.threadmodel.actor.mail.Mail;
import jforgame.threadmodel.actor.config.MailboxConfig;
import jforgame.threadmodel.actor.mail.PriorityMail;

import java.util.Comparator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * 优先级邮箱实现
 *
 * @author wupeng0528
 */
public class PriorityMailbox extends Mailbox {

    private final MailboxConfig config;

    public PriorityMailbox(MailboxConfig config) {
        super(createPriorityQueue(config.getCapacity()));
        this.config = config;
    }

    private static BlockingQueue<Mail> createPriorityQueue(int capacity) {
        return createPriorityQueue(capacity, new DefaultMailPriorityComparator());
    }

    private static BlockingQueue<Mail> createPriorityQueue(int capacity, Comparator<Mail> comparator) {
        return new PriorityBlockingQueue<>(capacity > 0 ? capacity : 1000, comparator);
    }

    public static class DefaultMailPriorityComparator implements Comparator<Mail> {
        @Override
        public int compare(Mail m1, Mail m2) {
            // 如果Mail实现了Priority接口，则使用优先级比较
            if (m1 instanceof PriorityMail && m2 instanceof PriorityMail) {
                PriorityMail p1 = (PriorityMail) m1;
                PriorityMail p2 = (PriorityMail) m2;
                return Integer.compare(p2.getPriority(), p1.getPriority()); // 高优先级在前
            }

            // 默认按照创建时间排序（FIFO）
            return Long.compare(m1.getCreatedTime(), m2.getCreatedTime());
        }
    }

    public MailboxConfig getConfig() {
        return config;
    }
}

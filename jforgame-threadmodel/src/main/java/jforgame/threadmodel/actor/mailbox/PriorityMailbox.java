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
            int priorityCompare = Integer.compare(resolvePriority(m2), resolvePriority(m1));
            if (priorityCompare != 0) {
                return priorityCompare;
            }

            // 同优先级下，按照创建时间排序（FIFO）
            return Long.compare(m1.getCreatedTime(), m2.getCreatedTime());
        }

        private int resolvePriority(Mail mail) {
            if (mail instanceof PriorityMail) {
                return ((PriorityMail) mail).getPriority();
            }
            return PriorityMail.NORMAL_PRIORITY;
        }
    }

    public MailboxConfig getConfig() {
        return config;
    }
}

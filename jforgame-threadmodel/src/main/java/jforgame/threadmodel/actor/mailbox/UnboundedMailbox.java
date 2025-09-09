package jforgame.threadmodel.actor.mailbox;

import jforgame.threadmodel.actor.mail.Mail;
import jforgame.threadmodel.actor.config.MailboxConfig;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 无界邮箱实现
 * @author wupeng0528
 */
public class UnboundedMailbox extends Mailbox {
    
    private final MailboxConfig config;
    
    public UnboundedMailbox(MailboxConfig config) {
        super(createUnboundedQueue());
        this.config = config;
    }
    
    private static BlockingQueue<Mail> createUnboundedQueue() {
        return new LinkedBlockingQueue<>();
    }
    
    public MailboxConfig getConfig() {
        return config;
    }
}

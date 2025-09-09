package jforgame.threadmodel.actor.mailbox;

import jforgame.threadmodel.actor.Mail;
import jforgame.threadmodel.actor.config.MailboxConfig;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 无界邮箱实现
 */
public class UnboundedMailbox extends jforgame.threadmodel.actor.Mailbox {
    
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

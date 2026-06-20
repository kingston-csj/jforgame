package jforgame.threadmodel.actor.mailbox;

import jforgame.threadmodel.actor.config.MailboxConfig;

/**
 * Mailbox factory, creates different types of mailboxes based on configuration
 */
public class MailboxFactory {

    /**
     * Create mailbox instance based on configuration
     *
     * @param config mailbox configuration
     * @return mailbox instance
     */
    public static Mailbox createMailbox(MailboxConfig config) {
        int mailboxType = config.getType();
        switch (mailboxType) {
            case MailboxConfig.TYPE_BOUNDED:
                // Bounded mailbox with limited capacity
                return new BoundedMailbox(config);
            case MailboxConfig.TYPE_UNBOUNDED:
                // Unbounded mailbox with unlimited capacity
                return new UnboundedMailbox(config);
            case MailboxConfig.TYPE_PRIORITY:
                // Priority mailbox sorted by priority
                return new PriorityMailbox(config);
            default:
                // Default to unbounded mailbox
                return new UnboundedMailbox(config);
        }
    }
}

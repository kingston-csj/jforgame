package jforgame.threadmodel.actor.mailbox;

import jforgame.threadmodel.actor.config.MailboxConfig;

public class MailboxFactory {

    public static Mailbox createMailbox(MailboxConfig config) {
        int mailboxType = config.getType();
        switch (mailboxType) {
            case MailboxConfig.TYPE_BOUNDED:
                return new BoundedMailbox(config);
            case MailboxConfig.TYPE_UNBOUNDED:
                return new UnboundedMailbox(config);
            default:
                // 默认使用无界邮箱
                return new UnboundedMailbox(config);
        }
    }
}

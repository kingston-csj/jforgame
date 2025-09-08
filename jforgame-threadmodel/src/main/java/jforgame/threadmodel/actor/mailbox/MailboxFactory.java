package jforgame.threadmodel.actor.mailbox;

import jforgame.threadmodel.actor.Mailbox;
import jforgame.threadmodel.actor.config.MailboxConfig;

public class MailboxFactory {
    
    public static Mailbox createMailbox(MailboxConfig config) {
        String mailboxType = config.getMailboxType();
        
        switch (mailboxType) {
            case "akka.dispatch.BoundedMailbox":
                return new BoundedMailbox(config);
                
            case "akka.dispatch.UnboundedMailbox":
                return new UnboundedMailbox(config);
                
            case "akka.dispatch.UnboundedPriorityMailbox":
                return new PriorityMailbox(config);

            default:
                // 默认使用无界邮箱
                return new UnboundedMailbox(config);
        }
    }
}

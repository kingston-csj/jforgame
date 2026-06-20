package jforgame.threadmodel.actor.mailbox;

import jforgame.threadmodel.actor.mail.Mail;
import jforgame.threadmodel.actor.config.MailboxConfig;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Bounded mailbox implementation
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
            // When queue is full, decide whether to discard or block based on configuration
            handleQueueFull(mail);
        }
    }

    /**
     * Handle queue full situation (reserved for future use)
     */
    private void handleQueueFull(Mail mail) {
        // Can decide strategy based on configuration:
        // 1. Discard new message
        // 2. Discard old message
        // 3. Block and wait
        // 4. Throw exception

        throw new IllegalStateException("BoundedMailbox queue is full, capacity: " + config.getCapacity());
    }

    public MailboxConfig getConfig() {
        return config;
    }
}

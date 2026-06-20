package jforgame.threadmodel.actor.config;

/**
 * Mailbox configuration class
 */
public class MailboxConfig {

    public static final int TYPE_UNBOUNDED = 1;
    public static final int TYPE_BOUNDED = 2;
    public static final int TYPE_PRIORITY = 3;

    /**
     * Mailbox type
     */
    private int type;

    /**
     * Mailbox capacity (-1 means unlimited)
     */
    private int capacity;


    public MailboxConfig() {
        // Default configuration
        this.type = TYPE_UNBOUNDED;
        this.capacity = 512;
    }

    public MailboxConfig(int type, int mailboxCapacity) {
        this.type = type;
        this.capacity = mailboxCapacity;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    /**
     * Check if it is a bounded mailbox
     *
     * @return whether it is bounded
     */
    public boolean isBounded() {
        return capacity > 0;
    }

    /**
     * Check if it is a priority mailbox
     *
     * @return whether it is priority
     */
    public boolean isPriority() {
        return type == TYPE_PRIORITY;
    }

    @Override
    public String toString() {
        return "MailboxConfig{" +
                "mailboxType='" + type + '\'' +
                ", mailboxCapacity=" + capacity +
                '}';
    }
}

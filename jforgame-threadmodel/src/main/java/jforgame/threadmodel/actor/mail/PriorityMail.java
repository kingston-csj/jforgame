package jforgame.threadmodel.actor.mail;

/**
 * Priority mail implementation
 */
public abstract class PriorityMail extends SimpleMail {

    /**
     * Mail priority
     */
    private final int priority;

    public PriorityMail(String type, int priority, Object... content) {
        super(type, content);
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }

    /**
     * High priority mail (higher number means higher priority)
     */
    public static final int HIGH_PRIORITY = 100;

    /**
     * Normal priority mail
     */
    public static final int NORMAL_PRIORITY = 50;

    /**
     * Low priority mail
     */
    public static final int LOW_PRIORITY = 10;
}

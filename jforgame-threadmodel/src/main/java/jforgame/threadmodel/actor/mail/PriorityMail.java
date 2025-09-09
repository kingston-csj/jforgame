package jforgame.threadmodel.actor.mail;

/**
 * 支持优先级的邮件实现
 */
public abstract class PriorityMail extends SimpleMail {

    /**
     * 邮件优先级
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
     * 高优先级邮件（数值越大优先级越高）
     */
    public static final int HIGH_PRIORITY = 100;

    /**
     * 普通优先级邮件
     */
    public static final int NORMAL_PRIORITY = 50;

    /**
     * 低优先级邮件
     */
    public static final int LOW_PRIORITY = 10;
}

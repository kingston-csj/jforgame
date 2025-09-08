package jforgame.threadmodel.actor;

/**
 * 邮箱类型枚举
 * 参考Akka的邮箱类型设计
 */
public enum MailboxType {
    /**
     * 无界队列邮箱
     */
    UNBOUNDED,
    
    /**
     * 有界队列邮箱
     */
    BOUNDED,
    
    /**
     * 优先级队列邮箱
     */
    PRIORITY,
    
    /**
     * 无界优先级队列邮箱
     */
    UNBOUNDED_PRIORITY
}
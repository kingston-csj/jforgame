package jforgame.threadmodel.actor;

/**
 * Actor属性配置类
 * 参考Akka的Props设计
 */
public class ActorProps {
    
    private MailboxType mailboxType = MailboxType.BOUNDED;
    private int mailboxCapacity = 512;
    private String actorName;
    
    private ActorProps() {}
    
    /**
     * 创建默认配置
     */
    public static ActorProps create() {
        return new ActorProps();
    }
    
    /**
     * 创建带名称的配置
     */
    public static ActorProps create(String actorName) {
        ActorProps props = new ActorProps();
        props.actorName = actorName;
        return props;
    }
    
    /**
     * 配置邮箱类型
     */
    public ActorProps withMailbox(MailboxType type) {
        this.mailboxType = type;
        return this;
    }
    
    /**
     * 配置邮箱类型和容量
     */
    public ActorProps withMailbox(MailboxType type, int capacity) {
        this.mailboxType = type;
        this.mailboxCapacity = capacity;
        return this;
    }
    
    /**
     * 配置Actor名称
     */
    public ActorProps withName(String name) {
        this.actorName = name;
        return this;
    }
    
    // Getters
    public MailboxType getMailboxType() {
        return mailboxType;
    }
    
    public int getMailboxCapacity() {
        return mailboxCapacity;
    }
    
    public String getActorName() {
        return actorName;
    }
}
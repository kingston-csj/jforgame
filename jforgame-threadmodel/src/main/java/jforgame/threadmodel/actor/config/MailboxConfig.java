package jforgame.threadmodel.actor.config;

/**
 * 邮箱配置类
 * @author wupeng0528
 */
public class MailboxConfig {

    public static final int TYPE_UNBOUNDED = 1;
    public static final int TYPE_BOUNDED = 2;
    public static final int TYPE_PRIORITY = 3;

    /**
     * 邮箱类型
     */
    private int type;

    /**
     * 邮箱容量 (-1 表示无限制)
     */
    private int capacity;


    public MailboxConfig() {
        // 默认配置
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
     * 判断是否为有界邮箱
     */
    public boolean isBounded() {
        return capacity > 0;
    }

    /**
     * 判断是否为优先级邮箱
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

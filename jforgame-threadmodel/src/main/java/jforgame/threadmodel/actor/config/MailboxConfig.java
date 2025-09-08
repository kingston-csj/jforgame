package jforgame.threadmodel.actor.config;

import java.time.Duration;
import java.util.Map;

/**
 * Akka风格的邮箱配置类
 */
public class MailboxConfig {
    
    /**
     * 邮箱类型
     */
    private String mailboxType;
    
    /**
     * 邮箱容量 (-1 表示无限制)
     */
    private int mailboxCapacity;
    
    /**
     * 推送超时时间
     */
    private Duration mailboxPushTimeout;
    
    /**
     * 暂存容量 (-1 表示无限制)
     */
    private int stashCapacity;
    
    /**
     * 扩展配置
     */
    private Map<String, Object> extendedConfig;
    
    public MailboxConfig() {
        // 默认配置
        this.mailboxType = "akka.dispatch.UnboundedMailbox";
        this.mailboxCapacity = 1000;
        this.mailboxPushTimeout = Duration.ofSeconds(10);
        this.stashCapacity = -1;
    }
    
    public MailboxConfig(String mailboxType, int mailboxCapacity, Duration mailboxPushTimeout) {
        this.mailboxType = mailboxType;
        this.mailboxCapacity = mailboxCapacity;
        this.mailboxPushTimeout = mailboxPushTimeout;
        this.stashCapacity = -1;
    }
    
    // Getters and Setters
    public String getMailboxType() {
        return mailboxType;
    }
    
    public void setMailboxType(String mailboxType) {
        this.mailboxType = mailboxType;
    }
    
    public int getMailboxCapacity() {
        return mailboxCapacity;
    }
    
    public void setMailboxCapacity(int mailboxCapacity) {
        this.mailboxCapacity = mailboxCapacity;
    }
    
    public Duration getMailboxPushTimeout() {
        return mailboxPushTimeout;
    }
    
    public void setMailboxPushTimeout(Duration mailboxPushTimeout) {
        this.mailboxPushTimeout = mailboxPushTimeout;
    }
    
    public int getStashCapacity() {
        return stashCapacity;
    }
    
    public void setStashCapacity(int stashCapacity) {
        this.stashCapacity = stashCapacity;
    }
    
    public Map<String, Object> getExtendedConfig() {
        return extendedConfig;
    }
    
    public void setExtendedConfig(Map<String, Object> extendedConfig) {
        this.extendedConfig = extendedConfig;
    }
    
    /**
     * 判断是否为有界邮箱
     */
    public boolean isBounded() {
        return "akka.dispatch.BoundedMailbox".equals(mailboxType) || 
               mailboxCapacity > 0;
    }
    
    /**
     * 判断是否为优先级邮箱
     */
    public boolean isPriority() {
        return mailboxType.contains("Priority");
    }
    
    /**
     * 判断是否为持久化邮箱
     */
    public boolean isDurable() {
        return mailboxType.contains("FileBased") || 
               mailboxType.contains("Durable");
    }
    
    @Override
    public String toString() {
        return "MailboxConfig{" +
               "mailboxType='" + mailboxType + '\'' +
               ", mailboxCapacity=" + mailboxCapacity +
               ", mailboxPushTimeout=" + mailboxPushTimeout +
               ", stashCapacity=" + stashCapacity +
               '}';
    }
}

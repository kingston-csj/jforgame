package jforgame.threadmodel.actor.config;

import java.util.HashMap;
import java.util.Map;

/**
 * Akka风格的Actor系统配置类
 */
public class ActorSystemConfig {
    
    /**
     * 默认分发器配置
     */
    private DispatcherConfig defaultDispatcher;
    
    /**
     * 默认邮箱配置
     */
    private MailboxConfig defaultMailbox;
    
    /**
     * 自定义分发器配置
     */
    private Map<String, DispatcherConfig> dispatchers;
    
    /**
     * 自定义邮箱配置
     */
    private Map<String, MailboxConfig> mailboxes;
    
    /**
     * Actor部署配置
     */
    private Map<String, ActorDeploymentConfig> deployments;
    
    /**
     * 日志级别
     */
    private String logLevel;
    
    /**
     * 是否启用JVM关闭钩子
     */
    private boolean jvmShutdownHooks;
    
    public ActorSystemConfig() {
        this.defaultDispatcher = new DispatcherConfig();
        this.defaultMailbox = new MailboxConfig();
        this.dispatchers = new HashMap<>();
        this.mailboxes = new HashMap<>();
        this.deployments = new HashMap<>();
        this.logLevel = "INFO";
        this.jvmShutdownHooks = true;
        
        // 初始化预定义配置
        initPredefinedConfigs();
    }
    
    /**
     * 初始化预定义的配置
     */
    private void initPredefinedConfigs() {
        // 有界邮箱配置
        MailboxConfig boundedMailbox = new MailboxConfig();
        boundedMailbox.setMailboxType("akka.dispatch.BoundedMailbox");
        boundedMailbox.setMailboxCapacity(512);
        mailboxes.put("bounded-mailbox", boundedMailbox);
        
        // 优先级邮箱配置
        MailboxConfig priorityMailbox = new MailboxConfig();
        priorityMailbox.setMailboxType("akka.dispatch.UnboundedPriorityMailbox");
        priorityMailbox.setMailboxCapacity(1000);
        mailboxes.put("priority-mailbox", priorityMailbox);
        
        // 持久化邮箱配置
        MailboxConfig durableMailbox = new MailboxConfig();
        durableMailbox.setMailboxType("akka.dispatch.FileBasedMailbox");
        durableMailbox.setMailboxCapacity(1000);
        mailboxes.put("durable-mailbox", durableMailbox);
        
        // 默认Actor部署配置
        ActorDeploymentConfig defaultDeployment = new ActorDeploymentConfig();
        defaultDeployment.setDispatcher("default-dispatcher");
        defaultDeployment.setMailbox("default-mailbox");
        deployments.put("default", defaultDeployment);
        
        // 玩家Actor部署配置
        ActorDeploymentConfig playerDeployment = new ActorDeploymentConfig();
        playerDeployment.setDispatcher("default-dispatcher");
        playerDeployment.setMailbox("bounded-mailbox");
        deployments.put("/player/*", playerDeployment);
        
        // 系统Actor部署配置
        ActorDeploymentConfig systemDeployment = new ActorDeploymentConfig();
        systemDeployment.setDispatcher("default-dispatcher");
        systemDeployment.setMailbox("default-mailbox");
        deployments.put("/system/*", systemDeployment);
        
        // 高优先级Actor部署配置
        ActorDeploymentConfig priorityDeployment = new ActorDeploymentConfig();
        priorityDeployment.setDispatcher("default-dispatcher");
        priorityDeployment.setMailbox("priority-mailbox");
        deployments.put("/priority/*", priorityDeployment);
    }
    
    /**
     * 根据Actor路径获取部署配置
     */
    public ActorDeploymentConfig getDeploymentConfig(String actorPath) {
        // 精确匹配
        ActorDeploymentConfig config = deployments.get(actorPath);
        if (config != null) {
            return config;
        }
        
        // 模式匹配
        for (Map.Entry<String, ActorDeploymentConfig> entry : deployments.entrySet()) {
            String pattern = entry.getKey();
            if (matchPattern(actorPath, pattern)) {
                return entry.getValue();
            }
        }
        
        // 返回默认配置
        return deployments.get("default");
    }
    
    /**
     * 简单的模式匹配
     */
    private boolean matchPattern(String path, String pattern) {
        if (pattern.endsWith("/*")) {
            String prefix = pattern.substring(0, pattern.length() - 2);
            return path.startsWith(prefix);
        }
        return path.equals(pattern);
    }
    
    /**
     * 获取邮箱配置
     */
    public MailboxConfig getMailboxConfig(String mailboxName) {
        if ("default-mailbox".equals(mailboxName)) {
            return defaultMailbox;
        }
        return mailboxes.getOrDefault(mailboxName, defaultMailbox);
    }
    
    /**
     * 获取分发器配置
     */
    public DispatcherConfig getDispatcherConfig(String dispatcherName) {
        if ("default-dispatcher".equals(dispatcherName)) {
            return defaultDispatcher;
        }
        return dispatchers.getOrDefault(dispatcherName, defaultDispatcher);
    }
    
    // Getters and Setters
    public DispatcherConfig getDefaultDispatcher() {
        return defaultDispatcher;
    }
    
    public void setDefaultDispatcher(DispatcherConfig defaultDispatcher) {
        this.defaultDispatcher = defaultDispatcher;
    }
    
    public MailboxConfig getDefaultMailbox() {
        return defaultMailbox;
    }
    
    public void setDefaultMailbox(MailboxConfig defaultMailbox) {
        this.defaultMailbox = defaultMailbox;
    }
    
    public Map<String, DispatcherConfig> getDispatchers() {
        return dispatchers;
    }
    
    public void setDispatchers(Map<String, DispatcherConfig> dispatchers) {
        this.dispatchers = dispatchers;
    }
    
    public Map<String, MailboxConfig> getMailboxes() {
        return mailboxes;
    }
    
    public void setMailboxes(Map<String, MailboxConfig> mailboxes) {
        this.mailboxes = mailboxes;
    }
    
    public Map<String, ActorDeploymentConfig> getDeployments() {
        return deployments;
    }
    
    public void setDeployments(Map<String, ActorDeploymentConfig> deployments) {
        this.deployments = deployments;
    }
    
    public String getLogLevel() {
        return logLevel;
    }
    
    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel;
    }
    
    public boolean isJvmShutdownHooks() {
        return jvmShutdownHooks;
    }
    
    public void setJvmShutdownHooks(boolean jvmShutdownHooks) {
        this.jvmShutdownHooks = jvmShutdownHooks;
    }
}

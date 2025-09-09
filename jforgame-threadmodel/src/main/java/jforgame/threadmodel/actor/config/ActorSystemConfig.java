package jforgame.threadmodel.actor.config;

import java.util.HashMap;
import java.util.Map;

/**
 * Actor系统配置类
 */
public class ActorSystemConfig {

    /**
     * 线程池核心线程数
     */
    private int corePoolSize = Runtime.getRuntime().availableProcessors();

    /**
     * 线程池最大线程数
     */
    private int maxPoolSize = Runtime.getRuntime().availableProcessors() * 2;

    /**
     * 线程池 keep-alive 时间
     */
    private int keepAliveSeconds = 60;

    /**
     * 系统共享Actor数量
     */
    private int systemSharedActorCount = 2;

    /**
     * 默认邮箱配置
     */
    private MailboxConfig defaultMailbox;

    /**
     * 自定义邮箱配置
     */
    private Map<String, MailboxConfig> mailboxes;

    /**
     * Actor部署配置
     */
    private Map<String, ActorDeploymentConfig> deployments;


    public ActorSystemConfig() {
        this.defaultMailbox = new MailboxConfig();
        this.mailboxes = new HashMap<>();
        this.deployments = new HashMap<>();

        // 初始化预定义配置
        initPredefinedConfigs();
    }

    /**
     * 初始化预定义的配置
     */
    private void initPredefinedConfigs() {
        // 有界邮箱配置
        MailboxConfig boundedMailbox = new MailboxConfig();
        boundedMailbox.setType(MailboxConfig.TYPE_BOUNDED);
        boundedMailbox.setCapacity(512);
        mailboxes.put("bounded-mailbox", boundedMailbox);

        // 优先级邮箱配置
        MailboxConfig priorityMailbox = new MailboxConfig();
        priorityMailbox.setType(MailboxConfig.TYPE_PRIORITY);
        priorityMailbox.setCapacity(1000);
        mailboxes.put("priority-mailbox", priorityMailbox);

        // 默认Actor部署配置
        ActorDeploymentConfig defaultDeployment = new ActorDeploymentConfig();
        defaultDeployment.setMailbox("default-mailbox");
        deployments.put("default", defaultDeployment);

        // 玩家Actor部署配置
        ActorDeploymentConfig playerDeployment = new ActorDeploymentConfig();
        playerDeployment.setMailbox("bounded-mailbox");
        deployments.put("/player/*", playerDeployment);

        // 系统Actor部署配置
        ActorDeploymentConfig systemDeployment = new ActorDeploymentConfig();
        systemDeployment.setMailbox("default-mailbox");
        deployments.put("/system/*", systemDeployment);

        // 高优先级Actor部署配置
        ActorDeploymentConfig priorityDeployment = new ActorDeploymentConfig();
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
        if ("default".equals(mailboxName)) {
            return defaultMailbox;
        }
        return mailboxes.getOrDefault(mailboxName, defaultMailbox);
    }

    public MailboxConfig getDefaultMailbox() {
        return defaultMailbox;
    }

    public void setDefaultMailbox(MailboxConfig defaultMailbox) {
        this.defaultMailbox = defaultMailbox;
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

    public int getCorePoolSize() {
        return corePoolSize;
    }

    public void setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    public void setMaxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
    }

    public int getKeepAliveSeconds() {
        return keepAliveSeconds;
    }

    public void setKeepAliveSeconds(int keepAliveSeconds) {
        this.keepAliveSeconds = keepAliveSeconds;
    }

    public int getSystemSharedActorCount() {
        return systemSharedActorCount;
    }

    public void setSystemSharedActorCount(int systemSharedActorCount) {
        this.systemSharedActorCount = systemSharedActorCount;
    }

}

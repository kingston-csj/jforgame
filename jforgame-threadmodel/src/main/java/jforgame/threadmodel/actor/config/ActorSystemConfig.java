package jforgame.threadmodel.actor.config;

import java.util.HashMap;
import java.util.Map;

/**
 * Actor system configuration class
 */
public class ActorSystemConfig {

    /**
     * Thread pool core thread count
     */
    private int corePoolSize = Runtime.getRuntime().availableProcessors();

    /**
     * Thread pool maximum thread count
     */
    private int maxPoolSize = Runtime.getRuntime().availableProcessors() * 2;

    /**
     * Thread pool keep-alive time
     */
    private int keepAliveSeconds = 60;

    /**
     * System shared Actor count
     */
    private int systemSharedActorCount = 2;

    /**
     * ActorSystem thread pool queue capacity (<=0 means unbounded)
     */
    private int queueCapacity = 0;

    /**
     * Default mailbox configuration
     */
    private MailboxConfig defaultMailbox;

    /**
     * Custom mailbox configurations
     */
    private Map<String, MailboxConfig> mailboxes;

    /**
     * Actor deployment configurations
     */
    private Map<String, ActorDeploymentConfig> deployments;

    public static final String MAILBOX_DEFAULT = "default-mailbox";
    public static final String MAILBOX_BOUNDED = "bounded-mailbox";
    public static final String MAILBOX_PRIORITY = "priority-mailbox";


    public ActorSystemConfig() {
        this.defaultMailbox = new MailboxConfig();
        this.mailboxes = new HashMap<>();
        this.deployments = new HashMap<>();

        // Initialize predefined configurations
        initPredefinedConfigs();
    }

    /**
     * Initialize predefined configurations
     */
    private void initPredefinedConfigs() {
        // Bounded mailbox configuration
        MailboxConfig boundedMailbox = new MailboxConfig();
        boundedMailbox.setType(MailboxConfig.TYPE_BOUNDED);
        boundedMailbox.setCapacity(512);
        mailboxes.put(MAILBOX_BOUNDED, boundedMailbox);

        // Priority mailbox configuration
        MailboxConfig priorityMailbox = new MailboxConfig();
        priorityMailbox.setType(MailboxConfig.TYPE_PRIORITY);
        priorityMailbox.setCapacity(1000);
        mailboxes.put(MAILBOX_PRIORITY, priorityMailbox);

        // Default Actor deployment configuration
        ActorDeploymentConfig defaultDeployment = new ActorDeploymentConfig();
        defaultDeployment.setMailbox(MAILBOX_DEFAULT);
        deployments.put("default", defaultDeployment);

        // Player Actor deployment configuration
        ActorDeploymentConfig playerDeployment = new ActorDeploymentConfig();
        playerDeployment.setMailbox("bounded-mailbox");
        deployments.put("/player/*", playerDeployment);

        // System Actor deployment configuration
        ActorDeploymentConfig systemDeployment = new ActorDeploymentConfig();
        systemDeployment.setMailbox("default-mailbox");
        deployments.put("/system/*", systemDeployment);

        // High priority Actor deployment configuration
        ActorDeploymentConfig priorityDeployment = new ActorDeploymentConfig();
        priorityDeployment.setMailbox("priority-mailbox");
        deployments.put("/priority/*", priorityDeployment);
    }

    /**
     * Get deployment configuration based on Actor path
     *
     * @param actorPath Actor path
     * @return deployment configuration
     */
    public ActorDeploymentConfig getDeploymentConfig(String actorPath) {
        // Exact match first
        ActorDeploymentConfig config = deployments.get(actorPath);
        if (config != null) {
            return config;
        }

        // Pattern matching, longest prefix wins
        ActorDeploymentConfig bestMatch = null;
        int bestPrefixLength = -1;
        for (Map.Entry<String, ActorDeploymentConfig> entry : deployments.entrySet()) {
            String pattern = entry.getKey();
            if (matchPattern(actorPath, pattern)) {
                int prefixLength = getPatternPrefixLength(pattern);
                if (prefixLength > bestPrefixLength) {
                    bestPrefixLength = prefixLength;
                    bestMatch = entry.getValue();
                }
            }
        }
        if (bestMatch != null) {
            return bestMatch;
        }

        // Return default configuration
        return deployments.get("default");
    }

    /**
     * Register custom deployment configuration, can also override default deployment configuration
     *
     * @param actorPath deployment configuration name
     * @param config    deployment configuration
     */
    public void registerDeploymentConfig(String actorPath, ActorDeploymentConfig config) {
        deployments.put(actorPath, config);
    }

    /**
     * Register custom mailbox configuration
     *
     * @param mailboxName mailbox name
     * @param config      mailbox configuration
     */
    public void registerMailboxConfig(String mailboxName, MailboxConfig config) {
        mailboxes.put(mailboxName, config);
    }

    /**
     * Simple pattern matching
     */
    private boolean matchPattern(String path, String pattern) {
        if (pattern.endsWith("/*")) {
            String prefix = pattern.substring(0, pattern.length() - 2);
            return path.startsWith(prefix);
        }
        return path.equals(pattern);
    }

    private int getPatternPrefixLength(String pattern) {
        if (pattern.endsWith("/*")) {
            return pattern.length() - 2;
        }
        return pattern.length();
    }

    /**
     * Get mailbox configuration
     *
     * @param mailboxName mailbox name
     * @return mailbox configuration
     */
    public MailboxConfig getMailboxConfig(String mailboxName) {
        if (mailboxName == null || "default".equals(mailboxName) || MAILBOX_DEFAULT.equals(mailboxName)) {
            return defaultMailbox;
        }
        return mailboxes.getOrDefault(mailboxName, defaultMailbox);
    }

    public MailboxConfig getDefaultMailbox() {
        return defaultMailbox;
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

    public int getQueueCapacity() {
        return queueCapacity;
    }

    public void setQueueCapacity(int queueCapacity) {
        this.queueCapacity = queueCapacity;
    }

}

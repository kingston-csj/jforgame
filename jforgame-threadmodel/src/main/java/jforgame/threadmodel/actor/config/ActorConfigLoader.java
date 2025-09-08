package jforgame.threadmodel.actor.config;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.Properties;

/**
 * 从配置文件加载Actor系统配置的工具类
 */
public class ActorConfigLoader {
    
    /**
     * 从classpath加载配置文件
     */
    public static ActorSystemConfig loadFromClasspath(String configPath) {
        try (InputStream inputStream = ActorConfigLoader.class.getClassLoader().getResourceAsStream(configPath)) {
            if (inputStream == null) {
                throw new IllegalArgumentException("Configuration file not found: " + configPath);
            }
            
            Properties properties = new Properties();
            properties.load(inputStream);
            
            return parseConfiguration(properties);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load configuration from: " + configPath, e);
        }
    }
    
    /**
     * 解析Properties配置
     */
    private static ActorSystemConfig parseConfiguration(Properties properties) {
        ActorSystemConfig config = new ActorSystemConfig();
        
        // 解析默认分发器配置
        DispatcherConfig defaultDispatcher = config.getDefaultDispatcher();
        defaultDispatcher.setCorePoolSize(getIntProperty(properties, "akka.actor.default-dispatcher.core-pool-size", defaultDispatcher.getCorePoolSize()));
        defaultDispatcher.setMaxPoolSize(getIntProperty(properties, "akka.actor.default-dispatcher.max-pool-size", defaultDispatcher.getMaxPoolSize()));
        defaultDispatcher.setKeepAliveTime(Duration.ofSeconds(getIntProperty(properties, "akka.actor.default-dispatcher.keep-alive-time", 60)));
        defaultDispatcher.setTaskQueueSize(getIntProperty(properties, "akka.actor.default-dispatcher.task-queue-size", -1));
        
        // 解析默认邮箱配置
        MailboxConfig defaultMailbox = config.getDefaultMailbox();
        defaultMailbox.setMailboxType(properties.getProperty("akka.actor.default-mailbox.mailbox-type", defaultMailbox.getMailboxType()));
        defaultMailbox.setMailboxCapacity(getIntProperty(properties, "akka.actor.default-mailbox.mailbox-capacity", defaultMailbox.getMailboxCapacity()));
        defaultMailbox.setMailboxPushTimeout(Duration.ofSeconds(getIntProperty(properties, "akka.actor.default-mailbox.mailbox-push-timeout-time", 10)));
        
        // 解析有界邮箱配置
        MailboxConfig boundedMailbox = new MailboxConfig();
        boundedMailbox.setMailboxType(properties.getProperty("akka.actor.bounded-mailbox.mailbox-type", "akka.dispatch.BoundedMailbox"));
        boundedMailbox.setMailboxCapacity(getIntProperty(properties, "akka.actor.bounded-mailbox.mailbox-capacity", 512));
        boundedMailbox.setMailboxPushTimeout(Duration.ofSeconds(getIntProperty(properties, "akka.actor.bounded-mailbox.mailbox-push-timeout-time", 10)));
        config.getMailboxes().put("bounded-mailbox", boundedMailbox);
        
        // 解析优先级邮箱配置
        MailboxConfig priorityMailbox = new MailboxConfig();
        priorityMailbox.setMailboxType(properties.getProperty("akka.actor.priority-mailbox.mailbox-type", "akka.dispatch.UnboundedPriorityMailbox"));
        priorityMailbox.setMailboxCapacity(getIntProperty(properties, "akka.actor.priority-mailbox.mailbox-capacity", 1000));
        priorityMailbox.setMailboxPushTimeout(Duration.ofSeconds(getIntProperty(properties, "akka.actor.priority-mailbox.mailbox-push-timeout-time", 10)));
        config.getMailboxes().put("priority-mailbox", priorityMailbox);
        
        // 设置日志级别
        config.setLogLevel(properties.getProperty("akka.loglevel", "INFO"));
        
        // 设置JVM关闭钩子
        config.setJvmShutdownHooks(getBooleanProperty(properties, "akka.jvm-shutdown-hooks", true));
        
        return config;
    }
    
    /**
     * 获取整数属性
     */
    private static int getIntProperty(Properties properties, String key, int defaultValue) {
        String value = properties.getProperty(key);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    /**
     * 获取布尔属性
     */
    private static boolean getBooleanProperty(Properties properties, String key, boolean defaultValue) {
        String value = properties.getProperty(key);
        if (value == null) {
            return defaultValue;
        }
        return Boolean.parseBoolean(value);
    }
    
    /**
     * 创建默认配置
     */
    public static ActorSystemConfig createDefaultConfig() {
        return new ActorSystemConfig();
    }
}

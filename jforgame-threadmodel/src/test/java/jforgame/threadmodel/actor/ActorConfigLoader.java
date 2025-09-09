package jforgame.threadmodel.actor;

import jforgame.threadmodel.actor.config.ActorSystemConfig;
import jforgame.threadmodel.actor.config.MailboxConfig;

import java.io.IOException;
import java.io.InputStream;
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

        // 解析默认邮箱配置
        MailboxConfig defaultMailbox = config.getDefaultMailbox();
        defaultMailbox.setType(MailboxConfig.TYPE_UNBOUNDED);
        defaultMailbox.setCapacity(getIntProperty(properties, "actor.default-mailbox.mailbox-capacity", defaultMailbox.getCapacity()));

        // 解析有界邮箱配置
        MailboxConfig boundedMailbox = new MailboxConfig();
        boundedMailbox.setType(MailboxConfig.TYPE_BOUNDED);
        boundedMailbox.setCapacity(getIntProperty(properties, "actor.bounded-mailbox.mailbox-capacity", 512));
        config.getMailboxes().put("bounded-mailbox", boundedMailbox);

        // 解析优先级邮箱配置
        MailboxConfig priorityMailbox = new MailboxConfig();
        priorityMailbox.setType(MailboxConfig.TYPE_PRIORITY);
        priorityMailbox.setCapacity(getIntProperty(properties, "actor.priority-mailbox.mailbox-capacity", 1000));
        config.getMailboxes().put("priority-mailbox", priorityMailbox);

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

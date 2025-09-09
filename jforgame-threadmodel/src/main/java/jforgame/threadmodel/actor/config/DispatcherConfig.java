package jforgame.threadmodel.actor.config;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Akka风格的分发器配置类
 */
public class DispatcherConfig {
    
    /**
     * 分发器类型
     */
    private String type;
    
    /**
     * 执行器类型
     */
    private String executor;
    
    /**
     * 核心线程池大小
     */
    private int corePoolSize;
    
    /**
     * 最大线程池大小
     */
    private int maxPoolSize;
    
    /**
     * 线程保活时间
     */
    private Duration keepAliveTime;
    
    /**
     * 任务队列大小 (-1 表示无限制)
     */
    private int taskQueueSize;
    
    /**
     * 任务队列类型
     */
    private String taskQueueType;
    
    /**
     * 是否允许核心线程超时
     */
    private boolean allowCoreTimeout;
    
    /**
     * 扩展配置
     */
    private Map<String, Object> extendedConfig;
    
    public DispatcherConfig() {
        // 默认配置
        this.type = "Dispatcher";
        this.executor = "thread-pool-executor";
        this.corePoolSize = Runtime.getRuntime().availableProcessors();
        this.maxPoolSize = Runtime.getRuntime().availableProcessors() * 2;
        this.keepAliveTime = Duration.ofSeconds(60);
        this.taskQueueSize = -1;
        this.taskQueueType = "linked";
        this.allowCoreTimeout = true;
        this.extendedConfig = new HashMap<>();
    }
    
    public DispatcherConfig(String type, String executor, int corePoolSize, int maxPoolSize) {
        this();
        this.type = type;
        this.executor = executor;
        this.corePoolSize = corePoolSize;
        this.maxPoolSize = maxPoolSize;
    }
    
    // Getters and Setters
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getExecutor() {
        return executor;
    }
    
    public void setExecutor(String executor) {
        this.executor = executor;
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
    
    public Duration getKeepAliveTime() {
        return keepAliveTime;
    }
    
    public void setKeepAliveTime(Duration keepAliveTime) {
        this.keepAliveTime = keepAliveTime;
    }
    
    public int getTaskQueueSize() {
        return taskQueueSize;
    }
    
    public void setTaskQueueSize(int taskQueueSize) {
        this.taskQueueSize = taskQueueSize;
    }
    
    public String getTaskQueueType() {
        return taskQueueType;
    }
    
    public void setTaskQueueType(String taskQueueType) {
        this.taskQueueType = taskQueueType;
    }
    
    public boolean isAllowCoreTimeout() {
        return allowCoreTimeout;
    }
    
    public void setAllowCoreTimeout(boolean allowCoreTimeout) {
        this.allowCoreTimeout = allowCoreTimeout;
    }
    
    public Map<String, Object> getExtendedConfig() {
        return extendedConfig;
    }
    
    public void setExtendedConfig(Map<String, Object> extendedConfig) {
        this.extendedConfig = extendedConfig;
    }
    
    @Override
    public String toString() {
        return "DispatcherConfig{" +
               "type='" + type + '\'' +
               ", executor='" + executor + '\'' +
               ", corePoolSize=" + corePoolSize +
               ", maxPoolSize=" + maxPoolSize +
               ", keepAliveTime=" + keepAliveTime +
               ", taskQueueSize=" + taskQueueSize +
               ", taskQueueType='" + taskQueueType + '\'' +
               ", allowCoreTimeout=" + allowCoreTimeout +
               '}';
    }
}

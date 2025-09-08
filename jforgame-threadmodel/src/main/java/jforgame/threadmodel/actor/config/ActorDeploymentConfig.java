package jforgame.threadmodel.actor.config;

import java.util.HashMap;
import java.util.Map;

/**
 * Actor部署配置类
 */
public class ActorDeploymentConfig {
    
    /**
     * 使用的分发器名称
     */
    private String dispatcher;
    
    /**
     * 使用的邮箱名称
     */
    private String mailbox;
    
    /**
     * 路由器配置
     */
    private String router;
    
    /**
     * 路由器池大小
     */
    private int routerPoolSize;
    
    /**
     * 扩展配置
     */
    private Map<String, Object> extendedConfig;
    
    public ActorDeploymentConfig() {
        this.dispatcher = "default-dispatcher";
        this.mailbox = "default-mailbox";
        this.routerPoolSize = 1;
        this.extendedConfig = new HashMap<>();
    }
    
    public ActorDeploymentConfig(String dispatcher, String mailbox) {
        this();
        this.dispatcher = dispatcher;
        this.mailbox = mailbox;
    }
    
    // Getters and Setters
    public String getDispatcher() {
        return dispatcher;
    }
    
    public void setDispatcher(String dispatcher) {
        this.dispatcher = dispatcher;
    }
    
    public String getMailbox() {
        return mailbox;
    }
    
    public void setMailbox(String mailbox) {
        this.mailbox = mailbox;
    }
    
    public String getRouter() {
        return router;
    }
    
    public void setRouter(String router) {
        this.router = router;
    }
    
    public int getRouterPoolSize() {
        return routerPoolSize;
    }
    
    public void setRouterPoolSize(int routerPoolSize) {
        this.routerPoolSize = routerPoolSize;
    }
    
    public Map<String, Object> getExtendedConfig() {
        return extendedConfig;
    }
    
    public void setExtendedConfig(Map<String, Object> extendedConfig) {
        this.extendedConfig = extendedConfig;
    }
    
    @Override
    public String toString() {
        return "ActorDeploymentConfig{" +
               "dispatcher='" + dispatcher + '\'' +
               ", mailbox='" + mailbox + '\'' +
               ", router='" + router + '\'' +
               ", routerPoolSize=" + routerPoolSize +
               '}';
    }
}

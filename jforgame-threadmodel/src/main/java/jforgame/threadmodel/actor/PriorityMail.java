package jforgame.threadmodel.actor;

/**
 * 优先级邮件接口
 * 实现此接口的邮件可以在优先级队列中按优先级排序
 */
public interface PriorityMail {
    
    /**
     * 获取邮件优先级
     * 数值越大优先级越高
     * @return 优先级值
     */
    int getPriority();
}
package jforgame.threadmodel.actor;

import jforgame.threadmodel.BaseTask;

/**
 * 邮件抽象基类
 * 所有投递到Actor邮箱的消息都应该继承此类
 */
public abstract class Mail extends BaseTask {

    /**
     * 邮件创建时间
     */
    protected final long createdTime;

    /**
     * 发送者（可选，用于追踪）
     */
    protected Actor sender;

    /**
     * 接收者（可选，用于追踪）
     */
    protected Actor receiver;

    public Mail() {
        this.createdTime = System.currentTimeMillis();
    }

    /**
     * 邮件处理逻辑，子类必须实现
     * 负责执行具体的业务逻辑
     */
    @Override
    public abstract void action();


    /**
     * 设置目标
     */
    public void setSender(Actor sender) {
        this.sender = sender;
    }

    /**
     * 获取目标
     */
    public Actor getSender() {
        return sender;
    }

    /**
     * 设置发送者
     */
    public void setReceiver(Actor receiver) {
        this.receiver = receiver;
    }

    /**
     * 获取发送者
     */
    public Actor getReceiver() {
        return receiver;
    }

    /**
     * 获取邮件创建时间
     */
    public long getCreatedTime() {
        return createdTime;
    }


}

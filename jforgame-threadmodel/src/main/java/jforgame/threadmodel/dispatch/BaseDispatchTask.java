package jforgame.threadmodel.dispatch;


import jforgame.threadmodel.BaseTask;

/**
 * 基于分发模型的任务
 */
public abstract class BaseDispatchTask extends BaseTask {

    /**
     * 任务分发的key，该key用于绑定具体的线程
     */
    protected long dispatchKey;

    public void setDispatchKey(long dispatchKey) {
        this.dispatchKey = dispatchKey;
    }

    public long getDispatchKey() {
        return dispatchKey;
    }

}

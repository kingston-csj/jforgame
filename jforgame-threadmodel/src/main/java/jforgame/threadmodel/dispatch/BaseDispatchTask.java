package jforgame.threadmodel.dispatch;


import jforgame.threadmodel.BaseTask;

/**
 * Task based on dispatch model
 */
public abstract class BaseDispatchTask extends BaseTask {

    /**
     * Task dispatch key, used to bind to a specific thread
     */
    protected long dispatchKey;

    public void setDispatchKey(long dispatchKey) {
        this.dispatchKey = dispatchKey;
    }

    public long getDispatchKey() {
        return dispatchKey;
    }

}

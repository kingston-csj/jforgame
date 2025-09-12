package jforgame.socket.share.task;

/**
 * 线程池的基本任务单元
 * {@link jforgame.socket.share.ThreadModel#accept(BaseGameTask)}
 */
public abstract class BaseGameTask implements Runnable {

    /**
     * 任务开始时间
     */
    private long startTime;

    /**
     * 任务结束时间
     */
    private long endTime;

    /**
     * 任务分发的key，该key用于绑定具体的线程
     */
    protected long dispatchKey;

    /**
     * 任务真正执行
     */
    public abstract void action();

    @Override
    public final void run() {
        this.startTime = System.currentTimeMillis();
        action();
        this.endTime = System.currentTimeMillis();
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public long getDispatchKey() {
        return dispatchKey;
    }

    public void setDispatchKey(long dispatchKey) {
        this.dispatchKey = dispatchKey;
    }

    public String getName() {
        return getClass().getSimpleName();
    }
}

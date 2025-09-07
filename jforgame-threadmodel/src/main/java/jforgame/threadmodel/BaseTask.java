package jforgame.threadmodel;


/**
 * 线程池的基本任务单元
 */
public abstract class BaseTask implements Runnable {

    /**
     * 任务开始时间
     */
    protected long startTime;

    /**
     * 任务结束时间
     */
    protected long endTime;

    /**
     * 具体业务执行
     */
    public abstract void action();

    /**
     * 任务执行切面，注意：子类无法覆盖该方法
     */
    @Override
    public final void run() {
        this.startTime = System.currentTimeMillis();
        action();
        this.endTime = System.currentTimeMillis();
    }

    public String getName() {
        return getClass().getSimpleName();
    }
}

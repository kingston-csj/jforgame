package jforgame.threadmodel;


/**
 * Base task unit for thread pool
 */
public abstract class BaseTask implements Runnable {

    /**
     * Task start time
     */
    protected long startTime;

    /**
     * Task end time
     */
    protected long endTime;

    /**
     * Actual business execution
     */
    public abstract void action();

    /**
     * Task execution aspect, note: subclasses cannot override this method
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

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }
}

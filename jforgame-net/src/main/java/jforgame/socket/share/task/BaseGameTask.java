package jforgame.socket.share.task;

public abstract class BaseGameTask implements Runnable {

    private long startTime;

    private long endTime;

    protected long dispatchKey;

    public abstract void action();

    @Override
    public void run() {
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

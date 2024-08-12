package jforgame.runtime.thread;

public class ThreadVo {
    private long id;
    private String name;
    private String group;
    private int priority;
    private Thread.State state;
    private double cpu;
    private long deltaTime;
    private long time;
    private boolean interrupted;
    private boolean daemon;

    public ThreadVo() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public Thread.State getState() {
        return state;
    }

    public void setState(Thread.State state) {
        this.state = state;
    }

    public double getCpu() {
        return cpu;
    }

    public void setCpu(double cpu) {
        this.cpu = cpu;
    }

    public long getDeltaTime() {
        return deltaTime;
    }

    public void setDeltaTime(long deltaTime) {
        this.deltaTime = deltaTime;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isInterrupted() {
        return interrupted;
    }

    public void setInterrupted(boolean interrupted) {
        this.interrupted = interrupted;
    }

    public boolean isDaemon() {
        return daemon;
    }

    public void setDaemon(boolean daemon) {
        this.daemon = daemon;
    }

}

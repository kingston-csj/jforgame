package jforgame.runtime.thread;

public class DeadThreadInfo {

    private String name;

    private StackTraceElement[] stackTrace;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public StackTraceElement[] getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(StackTraceElement[] stackTrace) {
        this.stackTrace = stackTrace;
    }
}

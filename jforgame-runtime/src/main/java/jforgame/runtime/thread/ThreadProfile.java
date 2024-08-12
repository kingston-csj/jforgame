package jforgame.runtime.thread;

import java.util.List;
import java.util.Map;

public class ThreadProfile {

    private List<ThreadVo> threadStats;

    private Map<Thread.State, Integer> stateCountMap;

    public ThreadProfile(List<ThreadVo> threadStats, Map<Thread.State, Integer> stateCountMap) {
        this.threadStats = threadStats;
        this.stateCountMap = stateCountMap;
    }

    public List<ThreadVo> getThreadStats() {
        return threadStats;
    }

    public void setThreadStats(List<ThreadVo> threadStats) {
        this.threadStats = threadStats;
    }

    public Map<Thread.State, Integer> getStateCountMap() {
        return stateCountMap;
    }

    public void setStateCountMap(Map<Thread.State, Integer> stateCountMap) {
        this.stateCountMap = stateCountMap;
    }

}

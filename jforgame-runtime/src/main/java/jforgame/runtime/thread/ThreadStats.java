package jforgame.runtime.thread;

import jforgame.runtime.util.ThreadUtil;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public  final class ThreadStats {

    private static ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();

    private static int sampleInterval = 200;

    /**
     * 线程总体概述
     */
    public static ThreadProfile threadProfile() {
        List<ThreadVo> threads = ThreadUtil.getThreads();
        // 统计各种线程状态
        Map<Thread.State, Integer> stateCountMap = new LinkedHashMap<>();
        for (Thread.State s : Thread.State.values()) {
            stateCountMap.put(s, 0);
        }
        for (ThreadVo thread : threads) {
            Thread.State threadState = thread.getState();
            stateCountMap.compute(threadState, (k, count) -> count + 1);
        }

        //thread stats
        ThreadSampler threadSampler = new ThreadSampler();
        threadSampler.sample(threads);
        threadSampler.pause(sampleInterval);
        List<ThreadVo> threadStats = threadSampler.sample(threads);

        return new ThreadProfile(threadStats, stateCountMap);
    }

    /**
     * 显示前N条繁忙线程信息
     *
     * @param topNBusy       top N threads to monitor
     * @param lockedMonitors whether to use {@link ThreadMXBean#getThreadInfo(long[], boolean, boolean)} 's lockedMonitors and lockedSynchronizers param
     */
    public static List<BusyThreadInfo> topBusyThreads(int topNBusy, boolean lockedMonitors) {
        ThreadSampler threadSampler = new ThreadSampler();
        threadSampler.sample(ThreadUtil.getThreads());
        threadSampler.pause(sampleInterval);
        List<ThreadVo> threadStats = threadSampler.sample(ThreadUtil.getThreads());
        int limit = Math.min(threadStats.size(), topNBusy);

        List<ThreadVo> topNThreads = null;
        if (limit > 0) {
            topNThreads = threadStats.subList(0, limit);
        } else { // -1 for all threads
            topNThreads = threadStats;
        }

        List<Long> tids = new ArrayList<>(topNThreads.size());
        for (ThreadVo thread : topNThreads) {
            if (thread.getId() > 0) {
                tids.add(thread.getId());
            }
        }

        ThreadInfo[] threadInfos = threadMXBean.getThreadInfo(tids.stream().mapToLong(Long::longValue).toArray(), lockedMonitors, lockedMonitors);
        //threadInfo with cpuUsage
        List<BusyThreadInfo> busyThreadInfos = new ArrayList<>(topNThreads.size());
        for (ThreadVo thread : topNThreads) {
            ThreadInfo threadInfo = findThreadInfoById(threadInfos, thread.getId());
            BusyThreadInfo busyThread = new BusyThreadInfo(thread, threadInfo);
            busyThreadInfos.add(busyThread);
        }

        return busyThreadInfos;
    }

    private static ThreadInfo findThreadInfoById(ThreadInfo[] threadInfos, long id) {
        for (ThreadInfo threadInfo : threadInfos) {
            if (threadInfo != null && threadInfo.getThreadId() == id) {
                return threadInfo;
            }
        }
        return null;
    }

    /**
     * 查找死锁线程信息
     * @return 未检测到死锁，返回Null；否则，返回ThreadInfo数组
     */
    public static ThreadInfo[] findDeadLockThreads() {
        long[] deadlockedThreads = threadMXBean.findDeadlockedThreads();
        if (deadlockedThreads == null) {
            return null;
        }
        return threadMXBean.getThreadInfo(deadlockedThreads);
    }

}

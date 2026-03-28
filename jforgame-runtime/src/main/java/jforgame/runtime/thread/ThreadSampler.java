package jforgame.runtime.thread;

import sun.management.HotspotThreadMBean;
import sun.management.ManagementFactoryHelper;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ThreadSampler {

    private static ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
    private static HotspotThreadMBean hotspotThreadMBean;
    private static boolean hotspotThreadMBeanEnable = true;

    private Map<Long, Long> lastCpuTimesById = new HashMap<>();

    private Map<String, Long> lastInternalThreadCpuTimes = new HashMap<>();

    private long lastSampleTimeNanos;

    static {
        try {
            if (threadMXBean.isThreadCpuTimeSupported() && !threadMXBean.isThreadCpuTimeEnabled()) {
                threadMXBean.setThreadCpuTimeEnabled(true);
            }
        } catch (Throwable ignored) {
        }
    }

    public List<ThreadVo> sample(Collection<ThreadVo> originThreads) {
        List<ThreadVo> threads = new ArrayList<>(originThreads);

        // Sample CPU
        if (lastCpuTimesById.isEmpty() && lastInternalThreadCpuTimes.isEmpty()) {
            lastSampleTimeNanos = System.nanoTime();
            for (ThreadVo thread : threads) {
                if (thread.getId() > 0) {
                    long cpu = threadMXBean.getThreadCpuTime(thread.getId());
                    lastCpuTimesById.put(thread.getId(), cpu);
                    thread.setTime(cpu / 1000000);
                }
            }

            // add internal threads
            Map<String, Long> internalThreadCpuTimes = getInternalThreadCpuTimes();
            if (internalThreadCpuTimes != null) {
                for (Map.Entry<String, Long> entry : internalThreadCpuTimes.entrySet()) {
                    String key = entry.getKey();
                    ThreadVo thread = createThreadVO(key);
                    thread.setTime(entry.getValue() / 1000000);
                    threads.add(thread);
                    lastInternalThreadCpuTimes.put(key, entry.getValue());
                }
            }

            //sort by time
            threads.sort((o1, o2) -> Long.compare(o2.getTime(), o1.getTime()));

            return threads;
        }

        // Resample
        long newSampleTimeNanos = System.nanoTime();
        Map<Long, Long> newCpuTimesById = new HashMap<>(threads.size());
        for (ThreadVo thread : originThreads) {
            if (thread.getId() > 0) {
                long cpu = threadMXBean.getThreadCpuTime(thread.getId());
                newCpuTimesById.put(thread.getId(), cpu);
            }
        }
        // internal threads
        Map<String, Long> newInternalCpuTimes = new HashMap<>();
        Map<String, Long> newInternalThreadCpuTimes = getInternalThreadCpuTimes();
        if (newInternalThreadCpuTimes != null) {
            for (Map.Entry<String, Long> entry : newInternalThreadCpuTimes.entrySet()) {
                ThreadVo threadVO = createThreadVO(entry.getKey());
                threads.add(threadVO);
                newInternalCpuTimes.put(entry.getKey(), entry.getValue());
            }
        }

        // Compute delta time
        final Map<ThreadVo, Long> deltas = new HashMap<ThreadVo, Long>(threads.size());
        for (ThreadVo thread : threads) {
            long time1;
            long time2;
            if (thread.getId() > 0) {
                time1 = lastCpuTimesById.getOrDefault(thread.getId(), 0L);
                time2 = newCpuTimesById.getOrDefault(thread.getId(), 0L);
            } else {
                time1 = lastInternalThreadCpuTimes.getOrDefault(thread.getName(), 0L);
                time2 = newInternalCpuTimes.getOrDefault(thread.getName(), 0L);
            }
            if (time1 == -1) {
                time1 = time2;
            } else if (time2 == -1) {
                time2 = time1;
            }
            long delta = time2 - time1;
            deltas.put(thread, delta);
        }

        long sampleIntervalNanos = newSampleTimeNanos - lastSampleTimeNanos;

        // Compute cpu usage
        final HashMap<ThreadVo, Double> cpuUsages = new HashMap<>(threads.size());
        for (ThreadVo thread : threads) {
            double cpu = sampleIntervalNanos == 0 ? 0 : (Math.rint(deltas.get(thread) * 10000.0 / sampleIntervalNanos) / 100.0);
            cpuUsages.put(thread, cpu);
        }

        // Sort by CPU time : should be a rendering hint...
        threads.sort((o1, o2) -> Long.compare(deltas.get(o2), deltas.get(o1)));

        for (ThreadVo thread : threads) {
            //nanos to mills
            long cpuTimeNanos;
            if (thread.getId() > 0) {
                cpuTimeNanos = newCpuTimesById.getOrDefault(thread.getId(), 0L);
            } else {
                cpuTimeNanos = newInternalCpuTimes.getOrDefault(thread.getName(), 0L);
            }
            long timeMills = cpuTimeNanos / 1000000;
            long deltaTime = deltas.get(thread) / 1000000;
            double cpu = cpuUsages.get(thread);

            thread.setCpu(cpu);
            thread.setTime(timeMills);
            thread.setDeltaTime(deltaTime);
        }
        lastCpuTimesById = newCpuTimesById;
        lastInternalThreadCpuTimes = newInternalCpuTimes;
        lastSampleTimeNanos = newSampleTimeNanos;

        return threads;
    }

    private Map<String, Long> getInternalThreadCpuTimes() {
        if (hotspotThreadMBeanEnable) {
            try {
                if (hotspotThreadMBean == null) {
                    hotspotThreadMBean = ManagementFactoryHelper.getHotspotThreadMBean();
                }
                return hotspotThreadMBean.getInternalThreadCpuTimes();
            } catch (Throwable e) {
                //ignore ex
                hotspotThreadMBeanEnable = false;
            }
        }
        return null;
    }

    private ThreadVo createThreadVO(String name) {
        ThreadVo threadVO = new ThreadVo();
        threadVO.setId(-1);
        threadVO.setName(name);
        threadVO.setPriority(-1);
        threadVO.setDaemon(true);
        threadVO.setInterrupted(false);
        return threadVO;
    }

    public void pause(long mills) {
        try {
            Thread.sleep(mills);
        } catch (InterruptedException ignored) {
        }
    }

}

package jforgame.runtime.memory;

import jforgame.runtime.util.StringUtils;

import java.lang.management.BufferPoolMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryType;
import java.lang.management.MemoryUsage;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static jforgame.runtime.memory.MemoryStatsVo.TYPE_HEAP;
import static jforgame.runtime.memory.MemoryStatsVo.TYPE_NON_HEAP;
import static jforgame.runtime.memory.MemoryStatsVo.TYPE_BUFFER_POOL;

public final class MemoryStats {

    public static Map<String, List<MemoryStatsVo>> memoryInfo() {
        List<MemoryPoolMXBean> memoryPoolMXBeans = ManagementFactory.getMemoryPoolMXBeans();
        Map<String, List<MemoryStatsVo>> memoryInfoMap = new LinkedHashMap<>();

        // heap
        MemoryUsage heapMemoryUsage = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
        List<MemoryStatsVo> heapMemEntries = new ArrayList<>();
        heapMemEntries.add(createMemoryEntryVO(TYPE_HEAP, TYPE_HEAP, heapMemoryUsage));
        for (MemoryPoolMXBean poolMXBean : memoryPoolMXBeans) {
            if (MemoryType.HEAP.equals(poolMXBean.getType())) {
                MemoryUsage usage = getUsage(poolMXBean);
                if (usage != null) {
                    String poolName = StringUtils.beautifyName(poolMXBean.getName());
                    heapMemEntries.add(createMemoryEntryVO(TYPE_HEAP, poolName, usage));
                }
            }
        }
        memoryInfoMap.put(TYPE_HEAP, heapMemEntries);

        // non-heap
        MemoryUsage nonHeapMemoryUsage = ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage();
        List<MemoryStatsVo> nonHeapMemEntries = new ArrayList<>();
        nonHeapMemEntries.add(createMemoryEntryVO(TYPE_NON_HEAP, TYPE_NON_HEAP, nonHeapMemoryUsage));
        for (MemoryPoolMXBean poolMXBean : memoryPoolMXBeans) {
            if (MemoryType.NON_HEAP.equals(poolMXBean.getType())) {
                MemoryUsage usage = getUsage(poolMXBean);
                if (usage != null) {
                    String poolName = StringUtils.beautifyName(poolMXBean.getName());
                    nonHeapMemEntries.add(createMemoryEntryVO(TYPE_NON_HEAP, poolName, usage));
                }
            }
        }
        memoryInfoMap.put(TYPE_NON_HEAP, nonHeapMemEntries);

        addBufferPoolMemoryInfo(memoryInfoMap);
        return memoryInfoMap;
    }

    private static MemoryUsage getUsage(MemoryPoolMXBean memoryPoolMXBean) {
        try {
            return memoryPoolMXBean.getUsage();
        } catch (InternalError e) {
            return null;
        }
    }

    private static void addBufferPoolMemoryInfo(Map<String, List<MemoryStatsVo>> memoryInfoMap) {
        try {
            List<MemoryStatsVo> bufferPoolMemEntries = new ArrayList<>();
            @SuppressWarnings("rawtypes") Class bufferPoolMXBeanClass = Class.forName("java.lang.management.BufferPoolMXBean");
            @SuppressWarnings("unchecked") List<BufferPoolMXBean> bufferPoolMXBeans = ManagementFactory.getPlatformMXBeans(bufferPoolMXBeanClass);
            for (BufferPoolMXBean mbean : bufferPoolMXBeans) {
                long used = mbean.getMemoryUsed();
                long total = mbean.getTotalCapacity();
                bufferPoolMemEntries.add(new MemoryStatsVo(TYPE_BUFFER_POOL, mbean.getName(), used, total, Long.MIN_VALUE));
            }
            memoryInfoMap.put(TYPE_BUFFER_POOL, bufferPoolMemEntries);
        } catch (ClassNotFoundException ignored) {
        }
    }

    private static MemoryStatsVo createMemoryEntryVO(String type, String name, MemoryUsage memoryUsage) {
        return new MemoryStatsVo(type, name, memoryUsage.getUsed(), memoryUsage.getCommitted(), memoryUsage.getMax());
    }

}

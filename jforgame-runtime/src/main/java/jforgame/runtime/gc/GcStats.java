package jforgame.runtime.gc;

import jforgame.runtime.util.StringUtil;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;

public final class GcStats {

    public static List<GcInfoVo> showGcInfo() {
        List<GarbageCollectorMXBean> garbageCollectorMxBeans = ManagementFactory.getGarbageCollectorMXBeans();
        List<GcInfoVo> gcInfos = new ArrayList<>();
        for (GarbageCollectorMXBean gcMXBean : garbageCollectorMxBeans) {
            String name = gcMXBean.getName();
            gcInfos.add(new GcInfoVo(StringUtil.beautifyName(name), gcMXBean.getCollectionCount(), gcMXBean.getCollectionTime()));
        }
        return gcInfos;
    }

}

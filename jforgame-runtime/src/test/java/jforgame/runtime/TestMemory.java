package jforgame.runtime;

import jforgame.commons.util.JsonUtil;
import jforgame.runtime.memory.MemoryStatsVo;
import jforgame.runtime.memory.MemoryStats;

import java.util.List;
import java.util.Map;

public class TestMemory {

    public static void main(String[] args) {
        Map<String, List<MemoryStatsVo>> stats = MemoryStats.memoryInfo();
        System.out.println(JsonUtil.object2String(stats));
    }
}

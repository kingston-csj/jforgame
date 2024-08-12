package jforgame.runtime;

import jforgame.commons.JsonUtil;
import jforgame.runtime.gc.GcInfoVo;
import jforgame.runtime.gc.GcStats;

import java.util.List;

public class TestGc {

    public static void main(String[] args) {
        List<GcInfoVo> gcInfoVos = GcStats.showGcInfo();
        System.out.println(JsonUtil.object2String(gcInfoVos));
    }
}

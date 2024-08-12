package jforgame.runtime;

import jforgame.commons.JsonUtil;
import jforgame.runtime.thread.BusyThreadInfo;
import jforgame.runtime.thread.ThreadProfile;
import jforgame.runtime.thread.ThreadStats;

import java.util.List;

public class TestThread {

    public static void main(String[] args) {
        ThreadProfile threadModel = ThreadStats.threadProfile();
        System.out.println(JsonUtil.object2String(threadModel));
        List<BusyThreadInfo> busyThreadInfos = ThreadStats.topBusyThreads(3, false);
        System.out.println(JsonUtil.object2String(busyThreadInfos));
    }
}

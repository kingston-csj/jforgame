package jforgame.runtime.util;

import jforgame.runtime.thread.ThreadVo;

import java.util.ArrayList;
import java.util.List;

public class ThreadUtil {

    public static List<ThreadVo> getThreads() {
        ThreadGroup root = getRoot();
        Thread[] threads = new Thread[root.activeCount()];
        while (root.enumerate(threads, true) == threads.length) {
            threads = new Thread[threads.length * 2];
        }
        List<ThreadVo> list = new ArrayList<ThreadVo>(threads.length);
        for (Thread thread : threads) {
            if (thread != null) {
                ThreadVo threadVO = createThreadVO(thread);
                list.add(threadVO);
            }
        }
        return list;
    }

    public static ThreadGroup getRoot() {
        ThreadGroup group = Thread.currentThread().getThreadGroup();
        ThreadGroup parent;
        while ((parent = group.getParent()) != null) {
            group = parent;
        }
        return group;
    }

    private static ThreadVo createThreadVO(Thread thread) {
        ThreadGroup group = thread.getThreadGroup();
        ThreadVo threadVO = new ThreadVo();
        threadVO.setId(thread.getId());
        threadVO.setName(thread.getName());
        threadVO.setGroup(group == null ? "" : group.getName());
        threadVO.setPriority(thread.getPriority());
        threadVO.setState(thread.getState());
        threadVO.setInterrupted(thread.isInterrupted());
        threadVO.setDaemon(thread.isDaemon());
        return threadVO;
    }

}

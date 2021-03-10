package jforgame.server.thread;

import jforgame.common.thread.NamedThreadFactory;
import jforgame.socket.actor.MailBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class ThreadCenter {

    private static Logger logger = LoggerFactory.getLogger(ThreadCenter.class);

    private static int CORE_SIZE = Runtime.getRuntime().availableProcessors();

    private static int SCHEDULER_CORE = 1;

    /**
     * 公共业务线程池
     */
    private static CommonBusinessExecutor COMMON_BUSINESS_CONTAINER = new CommonBusinessExecutor(
            "common-business", CORE_SIZE * 2);

    /**
     * 登录登出匿名任务队列
     */
    private static CommonMailGroup LOGIN_QUEUE = COMMON_BUSINESS_CONTAINER.createMailGroup("login-io", 4);

    /**
     * 调度任务线程池
     */
    private static ScheduledExecutorService scheduledService = Executors.newScheduledThreadPool(SCHEDULER_CORE,
            new NamedThreadFactory("common-scheduler"));

    public static MailBox createBusinessMailBox(String module) {
        return new MailBox(COMMON_BUSINESS_CONTAINER.ROOT_QUEUE, module);
    }

    public static CommonMailGroup getLoginQueue() {
        return LOGIN_QUEUE;
    }

    public static ScheduledExecutorService getScheduledExecutorService() {
        return scheduledService;
    }

}

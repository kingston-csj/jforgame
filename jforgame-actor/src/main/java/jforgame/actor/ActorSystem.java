package jforgame.actor;


import jforgame.commons.thread.NamedThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * actor系统
 * 负责actor的创建、销毁、调度等
 * 这里不负责管理所有的actor，避免对业务代码侵入性太强
 * 例如所有玩家登录登出的绑定困境，同时也避免了因为没有解除绑定而导致的内存泄漏
 */
public class ActorSystem {

    private static Logger logger = LoggerFactory.getLogger(ActorSystem.class);

    private static int CORE_SIZE = Runtime.getRuntime().availableProcessors();

    final AtomicBoolean running = new AtomicBoolean(true);


    /**
     * 线程池
     */
    private ThreadPoolExecutor pool;

    /**
     * 通用共享邮箱（可用于登录登出等通用场景）
     */
    private final SharedActor sharedActor;


    /**
     * 绑定共享邮箱
     *
     * @param key 邮箱key
     * @return MailBox实例
     */
    public Actor bindingSharedActor(long key) {
        return sharedActor.getSharedActor(key);
    }

    public ActorSystem() {
        this(CORE_SIZE * 2, 2);
    }

    /**
     * @param coreSize        公共业务线程池核心线程数
     * @param sharedGroupSize 共享邮箱组数量
     */
    public ActorSystem(int coreSize, int sharedGroupSize) {
        NamedThreadFactory threadFactory = new NamedThreadFactory("common-business");
        pool = new ThreadPoolExecutor(coreSize, coreSize, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>(), threadFactory);
        Actor[] actorGroup = new Actor[sharedGroupSize];
        for (int i = 0; i < sharedGroupSize; i++) {
            actorGroup[i] = new AbsActor(this, "shared-mailGroup");
        }
        sharedActor = new SharedActor(actorGroup);
    }

//    /**
//     * 创建业务邮箱
//     * 用于实名Actor的专属邮箱
//     *
//     * @param module 模块名称
//     * @return MailBox实例
//     */
//    public Mailbox createMailBox(String module) {
//        return new Mailbox(this, module);
//    }

    public void submit(Runnable task) {
        pool.submit(task);
    }

    /**
     * 优雅关闭
     */
    public void shutdown() {
        if (running.compareAndSet(true, false)) {
            logger.info("开始关闭ActorSystem...");
            try {
                // 关闭业务执行器
                pool.shutdown();
                logger.info("ActorSystem关闭完成");
            } catch (Exception e) {
                logger.error("ActorSystem关闭异常", e);
            }
        }
    }

    /**
     * 获取统计信息
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ActorSystem Statistics:\n");
        sb.append("Root Queue Size: ").append(pool.getTaskCount()).append("\n");
        sb.append("Shared Queue Workers: ").append(sharedActor.getWorkerSize()).append("\n");

        Actor[] boxGroup = sharedActor.getGroup();
        for (int i = 0; i < boxGroup.length; i++) {
            sb.append("Shared Worker[").append(i).append("]: ").append(boxGroup[i].getMailBox().getTaskSize()).append("\n");
        }
        return sb.toString();
    }

}

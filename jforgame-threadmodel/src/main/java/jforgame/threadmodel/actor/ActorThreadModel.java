package jforgame.threadmodel.actor;


import jforgame.commons.thread.NamedThreadFactory;
import jforgame.threadmodel.ThreadModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * actor线程模型
 * 负责actor调度
 * 这里不负责管理所有的actor，避免对业务代码侵入性太强
 * 例如所有玩家登录登出的绑定困境，同时也避免了因为没有解除绑定而导致的内存泄漏
 */
public class ActorThreadModel implements ThreadModel {

    private static final Logger logger = LoggerFactory.getLogger(ActorThreadModel.class);

    private static final int CORE_SIZE = Runtime.getRuntime().availableProcessors();

    final AtomicBoolean running = new AtomicBoolean(true);


    /**
     * 线程池
     */
    private final ThreadPoolExecutor threadPool;

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

    public ActorThreadModel() {
        // 使用actor，会严格控制业务线程的总数，避免按功能继续拆分线程池，因此线程数量可适当增加
        this(CORE_SIZE * 2, 2);
    }

    /**
     * @param coreSize        公共业务线程池核心线程数
     * @param sharedGroupSize 共享邮箱组数量
     */
    public ActorThreadModel(int coreSize, int sharedGroupSize) {
        NamedThreadFactory threadFactory = new NamedThreadFactory("message-business");
        threadPool = new ThreadPoolExecutor(coreSize, coreSize, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>(), threadFactory);
        Actor[] actorGroup = new Actor[sharedGroupSize];
        for (int i = 0; i < sharedGroupSize; i++) {
            actorGroup[i] = new AbsActor(this, "shared-actor");
        }
        sharedActor = new SharedActor(actorGroup);
    }

    @Override
    public void accept(Runnable task) {
        threadPool.submit(task);
    }

    /**
     * 优雅关闭
     */
    @Override
    public void shutDown() {
        if (running.compareAndSet(true, false)) {
            logger.info("开始关闭ActorSystem...");
            try {
                // 关闭业务执行器
                threadPool.shutdown();
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
        sb.append("Root Queue Size: ").append(threadPool.getTaskCount()).append("\n");
        sb.append("Shared Queue Workers: ").append(sharedActor.getWorkerSize()).append("\n");

        Actor[] boxGroup = sharedActor.getGroup();
        for (int i = 0; i < boxGroup.length; i++) {
            sb.append("Shared Worker[").append(i).append("]: ").append(boxGroup[i].getMailBox().getTaskSize()).append("\n");
        }
        return sb.toString();
    }

}

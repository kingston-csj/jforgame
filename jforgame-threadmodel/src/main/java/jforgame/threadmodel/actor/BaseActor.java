package jforgame.threadmodel.actor;

import jforgame.threadmodel.actor.config.ActorDeploymentConfig;
import jforgame.threadmodel.actor.config.ActorSystemConfig;
import jforgame.threadmodel.actor.config.MailboxConfig;
import jforgame.threadmodel.actor.mail.Mail;
import jforgame.threadmodel.actor.mailbox.Mailbox;
import jforgame.threadmodel.actor.mailbox.MailboxFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Actor基类，提供默认实现
 * 由于java不支持多继承，继承该类后，便无法继承其他类，
 * 若需要继承其他类，建议采用组合模式，把该类作为一个属性
 */
public class BaseActor implements Actor {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 任务堆积警戒值，超过这个值会预警
     */
    static int THRESHOLD = 100;

    /**
     * 单次处理最大任务数，防止其他任务饥饿
     */
    static int MAX_TASKS_PER_RUN = 50;

    /**
     * 绑定的邮箱
     */
    private Mailbox mailBox;

    /**
     * actor名称/路径
     */
    private final String actorPath;

    /**
     * 当前任务是否在parent队列里
     */
    private AtomicBoolean queued = new AtomicBoolean(false);

    /**
     * 所属的actor系统
     */
    private final ActorSystem actorSystem;


    public BaseActor(ActorSystem actorSystem, String actorPath) {
        this.actorSystem = actorSystem;
        this.actorPath = actorPath;
        ActorSystemConfig systemConfig = actorSystem.getSystemConfig();
        // 根据路径获取部署配置
        ActorDeploymentConfig deploymentConfig = systemConfig.getDeploymentConfig(actorPath);
        // 根据配置创建邮箱
        String mailboxName = deploymentConfig.getMailbox();
        MailboxConfig mailboxConfig = systemConfig.getMailboxConfig(mailboxName);
        this.mailBox = MailboxFactory.createMailbox(mailboxConfig);

        if (logger.isDebugEnabled()) {
            logger.debug("Created actor [{}] with mailbox type [{}] and capacity [{}]",
                    actorPath, mailboxConfig.getType(), mailboxConfig.getCapacity());
        }
    }

    @Override
    public String getModel() {
        return actorPath;
    }

    @Override
    public Mailbox getMailbox() {
        return mailBox;
    }

    @Override
    public void tell(Mail message, Actor sender) {
        Objects.requireNonNull(message);
        if (actorSystem.isShutdown()) {
            return;
        }
        message.setSender(sender);
        message.setReceiver(this);

        Mailbox mailBox = getMailbox();
        mailBox.receive(message);

        if (queued.compareAndSet(false, true)) {
            actorSystem.accept(this);
        }
    }

    /**
     * 负责遍历和调度Mailbox中的Mail, 原子性执行，不会出现并发问题
     */
    @Override
    public void run() {
        int size = mailBox.getTaskSize();
        if (size > THRESHOLD) {
            logger.warn("[{}]任务堆积严重，任务数量[{}]", actorPath, size);
        }

        try {
            // 防止任务一直占线
            // 限制单次处理任务数量，防止其他actor饥饿
            int processedCount = 0;
            Runnable mail;
            while ((mail = mailBox.poll()) != null && processedCount < MAX_TASKS_PER_RUN) {
                mail.run();
                processedCount++;
            }
        } catch (Exception e) {
            logger.error("[{}]任务执行异常", actorPath, e);
        } finally {
            // 无条件重置queued状态
            queued.set(false);

            // 如果还有消息，重新提交
            if (!mailBox.isEmpty() && actorSystem.running.get()) {
                if (queued.compareAndSet(false, true)) {
                    actorSystem.accept(this);
                }
            }
        }
    }

    public String getActorPath() {
        return actorPath;
    }
}

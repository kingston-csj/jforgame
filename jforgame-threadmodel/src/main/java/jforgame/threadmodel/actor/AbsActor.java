package jforgame.threadmodel.actor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Actor基类，提供默认实现
 * 由于java不支持多继承，继承该类后，便无法继承其他类，
 * 若需要继承其他类，建议采用组合模式，把该类作为一个属性
 */
public class AbsActor implements Actor {

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
     * actor名称
     */
    private final String actorName;

    /**
     * 当前任务是否在parent队列里
     */
    private AtomicBoolean queued = new AtomicBoolean(false);

    /**
     * 所属的actor系统
     */
    private ActorThreadModel actorSystem;

    public AbsActor(ActorThreadModel actorSystem) {
        this(actorSystem, ActorProps.create());
    }

    public AbsActor(ActorThreadModel actorSystem, String actorName) {
        this(actorSystem, ActorProps.create(actorName));
    }
    
    /**
     * 使用配置创建Actor
     */
    public AbsActor(ActorThreadModel actorSystem, ActorProps props) {
        this.actorSystem = actorSystem;
        this.actorName = props.getActorName() != null ? props.getActorName() : getClass().getSimpleName();
        this.mailBox = MailboxFactory.create(props.getMailboxType(), props.getMailboxCapacity());
    }

    @Override
    public String getModel() {
        return actorName;
    }


    @Override
    public Mailbox getMailBox() {
        return mailBox;
    }

    @Override
    public void tell(Mail message, Actor sender) {
        Objects.requireNonNull(message);
        if (!actorSystem.running.get()) {
            return;
        }
        message.setSender(sender);
        Mailbox mailBox = getMailBox();
        mailBox.receive(message);
        if (queued.compareAndSet(false, true)) {
            actorSystem.accept(this);
        }
    }


    /**
     * 处理邮件的默认实现
     * 子类可以重写此方法来处理具体的业务逻辑
     */
    public void receive(Mail mail) {
        // 默认调用邮件的action方法
        mail.action();
    }

    /**
     * 负责遍历和调度Mailbox中的Mail, 原子性执行，不会出现并发问题
     */
    @Override
    public void run() {
        // 防止任务一直占线
        int size = mailBox.getTaskSize();
        if (size > THRESHOLD) {
            logger.warn("[{}]任务堆积严重，任务数量[{}]", actorName, size);
        }

        try {
            // 限制单次处理任务数量，防止饥饿
            int processedCount = 0;
            Mail mail;
            while ((mail = (Mail) mailBox.mails.poll()) != null && processedCount < MAX_TASKS_PER_RUN) {
                receive(mail); // 调用receive方法处理邮件
                processedCount++;
            }
            // 如果还有任务，重新加入队列
            if (!mailBox.mails.isEmpty()) {
                if (queued.compareAndSet(false, true)) {
                    actorSystem.accept(this);
                }
            }
        } catch (Exception e) {
            logger.error("[{}]任务执行异常", actorName, e);
        } finally {
            // 只有在没有更多任务时才标记为未排队
            if (mailBox.mails.isEmpty()) {
                queued.compareAndSet(true, false);
            }
        }
    }

}

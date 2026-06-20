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
 * Base Actor class providing default implementation
 * Since Java does not support multiple inheritance, extending this class prevents inheriting from other classes.
 * If you need to extend another class, it is recommended to use the composition pattern,
 * making this class a field attribute.
 */
public class BaseActor implements Actor {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Task accumulation warning threshold, warnings are triggered when exceeding this value
     */
    static int THRESHOLD = 100;

    /**
     * Maximum number of tasks processed per run, to prevent other tasks from starvation
     */
    static int MAX_TASKS_PER_RUN = 50;

    /**
     * Bound mailbox
     */
    private Mailbox mailBox;

    /**
     * Actor name/path
     */
    private final String actorPath;

    /**
     * Whether current task is in the parent queue
     */
    private AtomicBoolean queued = new AtomicBoolean(false);

    /**
     * Whether actor is still in a state to receive messages
     */
    private final AtomicBoolean active = new AtomicBoolean(true);

    /**
     * Parent actor system
     */
    private final ActorSystem actorSystem;


    public BaseActor(ActorSystem actorSystem, String actorPath) {
        this.actorSystem = actorSystem;
        this.actorPath = actorPath;
        ActorSystemConfig systemConfig = actorSystem.getSystemConfig();
        // Get deployment configuration based on path
        ActorDeploymentConfig deploymentConfig = systemConfig.getDeploymentConfig(actorPath);
        // Create mailbox based on configuration
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
        if (actorSystem.isShutdown() || !active.get()) {
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
     * Responsible for iterating and dispatching Mail in Mailbox, atomic execution, no concurrency issues
     */
    @Override
    public void run() {
        if (!active.get()) {
            queued.set(false);
            return;
        }
        int size = mailBox.getTaskSize();
        if (size > THRESHOLD) {
            logger.warn("[{}] Heavy task accumulation, task count [{}]", actorPath, size);
        }

        try {
            // Prevent tasks from monopolizing execution
            // Limit single-run task count to prevent other actors from starvation
            int processedCount = 0;
            Runnable mail;
            while ((mail = mailBox.poll()) != null && processedCount < MAX_TASKS_PER_RUN) {
                mail.run();
                processedCount++;
            }
        } catch (Exception e) {
            logger.error("[{}] Task execution exception", actorPath, e);
        } finally {
            // Unconditionally reset queued state
            queued.set(false);

            // If there are still messages, resubmit
            if (active.get() && !mailBox.isEmpty() && actorSystem.running.get()) {
                if (queued.compareAndSet(false, true)) {
                    actorSystem.accept(this);
                }
            }
        }
    }

    public String getActorPath() {
        return actorPath;
    }

    void deactivate() {
        if (active.compareAndSet(true, false)) {
            mailBox.clear();
        }
    }

    boolean isActive() {
        return active.get();
    }
}

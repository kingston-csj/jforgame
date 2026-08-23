package jforgame.commons.persist;

import jforgame.commons.persist.dlq.DeadLetterQueue;
import org.quartz.CronScheduleBuilder;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ConcurrentModificationException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Persistence in cron expression form (based on Quartz)
 *
 * @since 3.4.0
 */
public class CronContainer extends BasePersistContainer {

    private static final Logger logger = LoggerFactory.getLogger(CronContainer.class);

    private final AtomicBoolean run = new AtomicBoolean(true);

    private final String name;

    // Store entities waiting for persistence, using entity's unique identifier as key
    private volatile ConcurrentHashMap<String, Entity<?>> entityQueue = new ConcurrentHashMap<>();

    // Quartz scheduler
    private final Scheduler scheduler;

    private SavingStrategy savingStrategy;

    /**
     * Dead letter queue manager, null means dead letter mechanism disabled (retry forever)
     */
    private DeadLetterQueue deadLetterQueue;

    private final static String keyInScheduler = "cronContainer";

    public CronContainer(String name, String cronExpression, SavingStrategy savingStrategy) {
        this(name, cronExpression, savingStrategy, null);
    }

    /**
     * Construct with dead letter queue support
     *
     * @param name            container name
     * @param cronExpression  cron expression for Quartz
     * @param savingStrategy  saving strategy
     * @param deadLetterQueue dead letter queue manager, null means retry forever (no DLQ)
     */
    public CronContainer(String name, String cronExpression, SavingStrategy savingStrategy, DeadLetterQueue deadLetterQueue) {
        this.name = name;
        this.savingStrategy = savingStrategy;
        this.deadLetterQueue = deadLetterQueue;
        try {
            // Create Quartz scheduler instance and start
            StdSchedulerFactory factory = getStdSchedulerFactory(name);
            scheduler = factory.getScheduler();
            scheduler.start();

            // Create scheduled task and register to scheduler, using passed cronExpression to configure trigger rules
            JobDetail jobDetail = JobBuilder.newJob(CronPersistJob.class)
                    .withIdentity("cronPersistJob", name)
                    .build();
            jobDetail.getJobDataMap().put(keyInScheduler, this);

            Trigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity("cronTrigger", name)
                    .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                    .build();

            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            logger.error("Failed to initialize scheduler for CronContainer [{}]", name, e);
            throw new RuntimeException("Failed to initialize scheduler", e);
        }
    }

    private static StdSchedulerFactory getStdSchedulerFactory(String name) throws SchedulerException {
        Properties props = new Properties();
        props.put("org.quartz.scheduler.instanceName", "jforgame-cron-container-" + name);
        props.put("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool");
        props.put("org.quartz.threadPool.threadCount", "1");
        props.put("org.quartz.threadPool.threadPriority", "5");
        props.put("org.quartz.jobStore.class", "org.quartz.simpl.RAMJobStore");
        StdSchedulerFactory factory = new StdSchedulerFactory();
        factory.initialize(props);
        return factory;
    }

    @Override
    public void receive(Entity<?> entity) {
        if (!run.get()) {
            return;
        }
        String key = entity.getKey();

        // Reject entities whose keys are already in dead letter queue,
        // preventing infinite retry loops. But keep the latest entity snapshot
        // in the dead letter for future reprocessing via deadLetterQueue.reprocess().
        if (isDeadKey(key)) {
            handleDeadEntityUpdate(key, entity);
            logger.warn("Entity rejected: key [{}] is in dead letter queue, use deadLetterQueue.reprocess() instead", key);
            return;
        }

        entityQueue.put(key, entity);
    }

    @Override
    protected void saveAllBeforeShutdown() {
        try {
            int poolSize = entityQueue.size();
            // Execute last persistence operation
            entityQueue.forEach((key, entity) -> {
                try {
                    savingStrategy.doSave(entity);
                } catch (Exception e) {
                    logger.error("Failed to save entity [{}] in CronContainer [{}]", key, name, e);
                }
            });
            // Close scheduler, stop scheduled task
            scheduler.shutdown(true);
            logger.error("{} container shutdown, save {} elements", name, poolSize);
        } catch (SchedulerException e) {
            logger.error("Failed to shutdown scheduler for CronContainer [{}]", name, e);
        }
    }

    @Override
    public int size() {
        return entityQueue.size();
    }

    /**
     * Get dead letter queue manager, may be null
     *
     * @return dead letter queue manager
     */
    public DeadLetterQueue getDeadLetterQueue() {
        return deadLetterQueue;
    }

    // Inner class implements Job interface, defines logic when scheduled task executes, will trigger according to cronExpression configuration rules
    public static class CronPersistJob implements Job {
        @Override
        public void execute(JobExecutionContext context) {
            CronContainer container = (CronContainer) context.getMergedJobDataMap().get(keyInScheduler);
            if (container == null || !container.run.get()) {
                return;
            }

            ConcurrentHashMap<String, Entity<?>> snapshot = container.entityQueue;
            container.entityQueue = new ConcurrentHashMap<>();
            for (Map.Entry<String, Entity<?>> entry : snapshot.entrySet()) {
                Entity<?> entity = entry.getValue();
                if (entity == null) {
                    continue;
                }
                String key = entry.getKey();
                try {
                    container.savingStrategy.doSave(entity);
                    if (container.deadLetterQueue != null) {
                        container.deadLetterQueue.onSaveSuccess(key);
                    }
                } catch (ConcurrentModificationException e1) {
                    container.receive(entity);
                } catch (Exception e) {
                    // Check if entity should be moved to dead letter queue
                    boolean movedToDeadLetter = false;
                    if (container.deadLetterQueue != null) {
                        movedToDeadLetter = container.deadLetterQueue.onSaveFailure(key, entity, e);
                    }
                    if (!movedToDeadLetter) {
                        // Re-enqueue for retry
                        container.receive(entity);
                    }
                    logger.error("Failed to save entity [{}] in CronContainer [{}]", key, container.name, e);
                }
            }
        }
    }
}

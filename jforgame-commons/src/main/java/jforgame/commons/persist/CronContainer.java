package jforgame.commons.persist;

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

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 以cron表达式（基于Quartz）的形式持久化
 *
 * @since 3.4.0
 */
public class CronContainer implements PersistContainer {

    private static final Logger logger = LoggerFactory.getLogger(CronContainer.class);

    private final AtomicBoolean run = new AtomicBoolean(true);

    private final String name;

    // 存储待持久化的实体，以实体的唯一标识作为键
    private volatile ConcurrentHashMap<String, Entity<?>> entityQueue = new ConcurrentHashMap<>();

    // Quartz调度器
    private final Scheduler scheduler;

    private SavingStrategy savingStrategy;

    private final static String keyInScheduler = "cronContainer";

    public CronContainer(String name, String cronExpression, SavingStrategy savingStrategy) {
        this.name = name;
        this.savingStrategy = savingStrategy;
        try {
            // 创建Quartz调度器实例并启动
            Properties props = new Properties();
            props.put("org.quartz.scheduler.instanceName", "jforgame-cron-container-" + name);
            props.put("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool");
            props.put("org.quartz.threadPool.threadCount", "1");
            props.put("org.quartz.threadPool.threadPriority", "5");
            props.put("org.quartz.jobStore.class", "org.quartz.simpl.RAMJobStore");
            StdSchedulerFactory factory = new StdSchedulerFactory();
            factory.initialize(props);
            scheduler = factory.getScheduler();
            scheduler.start();

            // 创建定时任务并注册到调度器中，使用传入的cronExpression配置触发规则
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

    @Override
    public void receive(Entity<?> entity) {
        if (!run.get()) {
            return;
        }
        String key = entity.getKey();
        entityQueue.put(key, entity);
    }

    @Override
    public void shutdownGraceful() {
        run.compareAndSet(true, false);
        try {
            // 执行最后一次持久化操作
            entityQueue.forEach((key, entity) -> {
                try {
                    savingStrategy.doSave(entity);
                } catch (Exception e) {
                    logger.error("Failed to save entity [{}] in CronContainer [{}]", key, name, e);
                }
            });
            // 关闭调度器，停止定时任务
            scheduler.shutdown(true);
            logger.info("Cron container [{}] close ok", name);
        } catch (SchedulerException e) {
            logger.error("Failed to shutdown scheduler for CronContainer [{}]", name, e);
        }
    }

    @Override
    public int size() {
        return entityQueue.size();
    }

    // 内部类实现Job接口，定义定时任务执行时的逻辑，会按照cronExpression配置的规则触发
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
                try {
                    container.savingStrategy.doSave(entity);
                } catch (Exception e) {
                    container.receive(entity);
                    logger.error("Failed to save entity [{}] in CronContainer [{}]", entry.getKey(), container.name, e);
                }
            }
        }
    }
}

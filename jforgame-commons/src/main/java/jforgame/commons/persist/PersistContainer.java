package jforgame.commons.persist;


/**
 * Persistence container
 * Mainly has four types:
 * 1. Queue-based persistence container, see {@link QueueContainer}
 * 2. Delay-based persistence container, see {@link DelayContainer}
 * 3. Time periodic scheduling (cron) persistence container, requires quartz library, see {@link CronContainer}
 * 4. Free combination of the above 3, refer to {@link QueueContainerGroup}
 */
public interface PersistContainer {

    /**
     * Receive entity
     *
     * @param entity entity object
     */
    void receive(Entity<?> entity);

    /**
     * Graceful shutdown, will ensure all data in waiting queue will be processed
     */
    void shutdownGraceful();

    /**
     * Current waiting queue size for database
     *
     * @return size queue size
     */
    int size();

}
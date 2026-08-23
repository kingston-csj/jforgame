package jforgame.commons.persist;

import jforgame.commons.persist.dlq.DeadLetterQueue;

import java.util.Arrays;

/**
 * Persistence in queue group form
 * Combine several queue containers into a queue group, perform modulo operation based on entity id, similar to database table partitioning strategy
 */
@Deprecated
public class QueueContainerGroup extends BasePersistContainer {

    /**
     * Container group
     */
    private QueueContainer[] group;

    public QueueContainerGroup(String name, SavingStrategy savingStrategy, int workers) {
        this(name, savingStrategy, workers, null);
    }

    /**
     * Construct with dead letter queue support
     *
     * @param name            group name
     * @param savingStrategy  saving strategy
     * @param workers         number of worker threads
     * @param deadLetterQueue dead letter queue manager, null means retry forever (no DLQ)
     */
    public QueueContainerGroup(String name, SavingStrategy savingStrategy, int workers, DeadLetterQueue deadLetterQueue) {
        group = new QueueContainer[workers];
        for (int i = 0; i < workers; i++) {
            QueueContainer work = new QueueContainer(name, savingStrategy, deadLetterQueue);
            group[i] = work;
        }
        this.name = name + "-group";
    }

    @Override
    public void receive(Entity<?> entity) {
        int index = Math.abs(entity.getId().hashCode()) % group.length;
        group[index].receive(entity);
    }

    @Override
    public int size() {
        int size = 0;
        for (QueueContainer queueContainer : group) {
            size += queueContainer.size();
        }
        return size;
    }

    @Override
    protected void saveAllBeforeShutdown() {
        for (QueueContainer queueContainer : group) {
            queueContainer.saveAllBeforeShutdown();
        }
    }

    @Override
    public String toString() {
        return String.format(
                "QueueContainerGroup{groupName='%s', workerCount=%d, children=%s}",
                this.name,
                group.length,
                Arrays.toString(group)
        );
    }
}
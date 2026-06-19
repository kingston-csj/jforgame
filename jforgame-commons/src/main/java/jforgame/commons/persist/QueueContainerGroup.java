package jforgame.commons.persist;

/**
 * Persistence in queue group form
 * Combine several queue containers into a queue group, perform modulo operation based on entity id, similar to database table partitioning strategy
 */
public class QueueContainerGroup extends BasePersistContainer {

    /**
     * Container group
     */
    private QueueContainer[] group;

    public QueueContainerGroup(String name, SavingStrategy savingStrategy, int workers) {
        group = new QueueContainer[workers];
        for (int i = 0; i < workers; i++) {
            QueueContainer work = new QueueContainer(name, savingStrategy);
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
}
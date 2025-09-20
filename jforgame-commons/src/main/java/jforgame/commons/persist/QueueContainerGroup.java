package jforgame.commons.persist;

/**
 * 以队列组的形式持久化
 * 将若干的队列容器组合成一个队列组，根据实体id进行求模运算，类似于数据库的分表策略
 */
public class QueueContainerGroup extends BasePersistContainer {

    /**
     * 容器组
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
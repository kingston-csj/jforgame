package jforgame.orm.asyncdb;

import jforgame.orm.entity.BaseEntity;

/**
 * 以队列组的形式持久化
 * 将若干的队列容器组合成一个队列组，根据实体id进行求模运算，类似于数据库的分表策略
 */
public class QueueContainerGroup extends QueueContainer {

    /**
     * 容器组
     */
    private QueueContainer[] group;

    public QueueContainerGroup(String name, SavingStrategy savingStrategy, int workers) {
        super(name, savingStrategy);
        group = new QueueContainer[workers];
        for (int i = 0; i < workers; i++) {
            QueueContainer work = new QueueContainer(name, savingStrategy);
            group[i] = work;
        }
    }

    @Override
    public void receive(BaseEntity<?> entity) {
        int index = entity.getId().hashCode() % group.length;
        group[index].receive(entity);
    }

}
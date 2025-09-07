package jforgame.orm.entity;

import jforgame.orm.core.DbStatus;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 拥有各种db状态的数据库实体对象
 */
public abstract class StatefulEntity extends Stateful {

    /**
     * 是否已经持久化
     */
    private final AtomicBoolean persistent = new AtomicBoolean(false);

    /**
     * 当次需要持久化的字段列表(增量更新)
     */
    protected Set<String> modifiedColumns = new HashSet<>();

    /**
     * 是否需要保存所有字段
     */
    protected AtomicBoolean saveAll = new AtomicBoolean();

    /**
     * 当前实体对象的db状态 - 使用 AtomicReference 替代 volatile
     */
    protected AtomicReference<DbStatus> statusRef = new AtomicReference<>(DbStatus.NORMAL);

    public Set<String> getAllModifiedColumns() {
        return modifiedColumns;
    }

    /**
     * 以增量模式添加需要更新的字段
     * 只针对状态为 Status#UPDATE 的实体对象
     * 注意，使用此方法时， 切记参数必须与数据库表的字段名 一致，否则数据保存不全
     * @param column 需要更新的字段名，必须与数据库表的字段名 一致
     */
    public void addModifiedColumn(String... column) {
        if (column == null || column.length == 0) {
            return;
        }
        modifiedColumns.addAll(Arrays.asList(column));
    }

    /**
     * 强制保存所有字段，例如在玩家登出的时候，为了保险起见，推荐保存所有字段
     */
    public void forceSaveAll() {
        saveAll.compareAndSet(false, true);
    }

    /**
     * 是否需要保存所有字段
     * 当saveAll为true 或 modifiedColumns为空时，需要保存所有字段
     * @return 是否需要保存所有字段
     */
    public boolean isSaveAll() {
        return saveAll.get() || modifiedColumns.isEmpty();
    }

    @Override
    public final DbStatus getStatus() {
        return this.statusRef.get();
    }

    @Override
    public final boolean isNormal() {
        return this.statusRef.get() == DbStatus.NORMAL;
    }

    @Override
    public final boolean isNew() {
        return this.statusRef.get() == DbStatus.INSERT;
    }

    @Override
    public final boolean isModified() {
        return this.statusRef.get() == DbStatus.UPDATE;
    }

    @Override
    public final boolean isSoftDeleted() {
        return this.statusRef.get() == DbStatus.DELETE;
    }

    @Override
    public void markAsNew() {
        // 使用 CAS 设置插入状态
        this.statusRef.set(DbStatus.INSERT);
    }

    @Override
    public final void markAsModified() {
        // 只有 NORMAL 状态才可以变更为 UPDATE
        this.statusRef.compareAndSet(DbStatus.NORMAL, DbStatus.UPDATE);
    }

    @Override
    public final void markAsSoftDeleted() {
        // 如果当前是 INSERT 状态，则设置为 NORMAL
        // 否则设置为 DELETE 状态
        this.statusRef.updateAndGet(currentStatus -> {
            if (currentStatus == DbStatus.INSERT) {
                return DbStatus.NORMAL;
            } else {
                return DbStatus.DELETE;
            }
        });
    }

    /**
     * 标记为已经持久化
     * 当一个实体从数据库中加载出来，意识着这个实体已经存在于数据库中
     */
    protected void markPersistent() {
        persistent.compareAndSet(false, true);
    }

    /**
     * 是否数据库已有对应的实体
     *
     * @return 是否数据库已有对应的实体
     */
    public boolean existedInDb() {
        return persistent.get();
    }

    /**
     * 自动变更状db状态
     */
    public void autoChangedStatus() {
        // 删除状态只能手动设置
        if (!isSoftDeleted()) {
            // 如果已经存在于数据库，则表示修改记录
            if (existedInDb()) {
                markAsModified();
            } else {
                markAsNew();
            }
        }
    }

}

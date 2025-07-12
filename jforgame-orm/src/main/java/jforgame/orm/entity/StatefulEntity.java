package jforgame.orm.entity;

import jforgame.orm.DbStatus;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public abstract class StatefulEntity extends Stateful {

    /**
     * 是否已经持久化
     */
    private AtomicBoolean persistent = new AtomicBoolean(false);

    /**
     * 当次需要持久化的字段列表
     */
    protected Set<String> columns = new HashSet<>();

    protected AtomicBoolean saveAll = new AtomicBoolean();

    /**
     * 是否已经在持久化状态
     */
    protected AtomicBoolean saving = new AtomicBoolean(false);

    /**
     * 当前实体对象的db状态 - 使用 AtomicReference 替代 volatile
     */
    protected AtomicReference<DbStatus> statusRef = new AtomicReference<>(DbStatus.NORMAL);

    public boolean isSaving() {
        return saving.get();
    }

    public void setSaving() {
        saving.compareAndSet(false, true);
    }

    public Set<String> savingColumns() {
        return columns;
    }

    public void forceSaveAll() {
        saveAll.compareAndSet(false, true);
    }

    public boolean isSaveAll() {
        return saveAll.get();
    }

    @Override
    public final DbStatus getStatus() {
        return this.statusRef.get();
    }

    @Override
    public final boolean isInsert() {
        return this.statusRef.get() == DbStatus.INSERT;
    }

    @Override
    public final boolean isUpdate() {
        return this.statusRef.get() == DbStatus.UPDATE;
    }

    @Override
    public final boolean isDelete() {
        return this.statusRef.get() == DbStatus.DELETE;
    }

    @Override
    public void setInsert() {
        // 使用 CAS 设置插入状态
        this.statusRef.set(DbStatus.INSERT);
    }

    @Override
    public final void setUpdate() {
        // 只有 NORMAL 状态才可以变更为 UPDATE
        this.statusRef.compareAndSet(DbStatus.NORMAL, DbStatus.UPDATE);
    }

    @Override
    public final void setDelete() {
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

    public final void resetDbStatus() {
        this.statusRef.set(DbStatus.NORMAL);
        this.columns.clear();
        this.saveAll.compareAndSet(true, false);
        this.saving.compareAndSet(true, false);
        markPersistent();
    }

    /**
     * 标记为已经持久化
     */
    public void markPersistent() {
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

    public void autoSetStatus() {
        // 删除状态只能手动设置
        if (!isDelete()) {
            // 如果已经存在于数据库，则表示修改记录
            if (existedInDb()) {
                setUpdate();
            } else {
                setInsert();
            }
        }
    }

}

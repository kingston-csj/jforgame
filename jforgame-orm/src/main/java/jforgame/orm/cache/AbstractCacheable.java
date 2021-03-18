package jforgame.orm.cache;

import jforgame.orm.utils.SqlUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class AbstractCacheable extends Cacheable {

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
    public synchronized final DbStatus getStatus() {
        return this.status;
    }

    @Override
    public synchronized final boolean isInsert() {
        return this.status == DbStatus.INSERT;
    }

    @Override
    public synchronized final boolean isUpdate() {
        return this.status == DbStatus.UPDATE;
    }

    @Override
    public synchronized final boolean isDelete() {
        return this.status == DbStatus.DELETE;
    }

    @Override
    public synchronized void setInsert() {
        this.status = DbStatus.INSERT;
    }

    @Override
    public synchronized final void setUpdate() {
        //只有该状态才可以变更为update
        if (this.status == DbStatus.NORMAL) {
            this.status = DbStatus.UPDATE;
        }
    }

    @Override
    public synchronized final void setDelete() {
        if (this.status == DbStatus.INSERT) {
            this.status = DbStatus.NORMAL;
        } else {
            this.status = DbStatus.DELETE;
        }
    }

    public synchronized final void resetDbStatus() {
        this.status = DbStatus.NORMAL;
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
     * @return
     */
    public boolean existedInDb() {
        return persistent.get();
    }

    @Override
    public final String getSaveSql() {
        autoSetStatus();
        return SqlUtils.getSaveSql(this);
    }

    private void autoSetStatus() {
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

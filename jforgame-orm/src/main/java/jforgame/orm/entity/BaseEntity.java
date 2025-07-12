package jforgame.orm.entity;

import jforgame.orm.DbStatus;

import java.io.Serializable;

/**
 * abstract base class for db entity
 *
 * @author kinson
 */
public abstract class BaseEntity<Id extends Comparable<Id> & Serializable> extends StatefulEntity
        implements Serializable {

    /**
     * 实体的主键属性最好定义为包装类型，防止属性与getter/setter方法类型不匹配
     * TODO 增加起服验证
     * entity id
     *
     * @return
     */
    public abstract Id getId();

    /**
     * 从数据库加载完成的钩子
     */
    public final void afterLoad() {
        markPersistent();
        onAfterLoad();
    }

    /**
     * 供子类使用的加载完成钩子
     * 避免子类无意覆盖了afterLoad方法
     */
    protected void onAfterLoad() {

    }

    /**
     * 在entity持久化之前，应该调用该方法
     */
    public final void beforeSave() {
        autoChangedStatus();
        this.onBeforeSave();
    }

    /**
     * 供子类使用的持久化前钩子
     * 避免子类无意覆盖了beforeSave方法
     */
    protected void onBeforeSave() {
    }

    /**
     * 当entity持久化之后，应该调用该方法
     */
    public final void afterSave() {
        this.statusRef.set(DbStatus.NORMAL);
        this.columns.clear();
        this.saveAll.compareAndSet(true, false);
        this.saving.compareAndSet(true, false);
        onAfterSave();
    }

    /**
     * 供子类使用的持久化后钩子
     * 避免子类无意覆盖了afterSave方法
     */
    protected void onAfterSave() {

    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + getId().hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        BaseEntity other = (BaseEntity) obj;
        if (getId() != other.getId()) {
            return false;
        }
        return true;
    }


    /**
     * 主键的字符串表示
     */
    public String getKey() {
        return getClass().getSimpleName() + "@" + getId().toString();
    }

}

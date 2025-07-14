package jforgame.orm.entity;

import jforgame.commons.persist.Entity;
import jforgame.orm.core.DbStatus;

import java.io.Serializable;

/**
 * 基本实体类
 * 所有需要持久化的实体类都应该继承该类
 * 特别注意以下几个钩子方法一定需要接入
 * 1. {@link #afterLoad()} 当实体从数据库加载完成后，应该调用该方法，主要是用于标记实体为持久化状态，用于自动识别实体是更新还是插入状态， 与 beforeSave配合使用
 * 2. {@link #beforeSave()} 当实体准备持久化前，应该调用该方法，主要是用于自动识别实体是更新还是插入状态， 与 afterLoad配合使用
 * 3. {@link #afterSave()} 当实体持久化完成后，应该调用该方法，用于重置初实体为普通状态
 */
public abstract class BaseEntity<Id extends Comparable<Id> & Serializable> extends StatefulEntity
        implements Entity<Id> {

    /**
     * 实体的主键属性，不能是基本类型，只能是包装类型，或者是String类型
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
        this.modifiedColumns.clear();
        this.saveAll.compareAndSet(true, false);
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
        return getId().equals(other.getId());
    }

}

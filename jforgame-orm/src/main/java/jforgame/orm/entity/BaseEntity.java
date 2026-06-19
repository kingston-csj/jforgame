package jforgame.orm.entity;

import jforgame.commons.persist.Entity;
import jforgame.orm.core.DbStatus;

import java.io.Serializable;

/**
 * Base entity class.
 * All entity classes that need to be persisted should inherit from this class.
 * Special attention to the following hook methods that must be properly integrated. The best way is to encapsulate them in the underlying layer, not guaranteed by business code.
 * 1. {@link #afterLoad()} When an entity is loaded from the database, this method should be called. Mainly used to mark the entity as persistent state, for automatically identifying whether the entity should be updated or inserted.
 * 2. {@link #beforeSave()} When an entity is about to be persisted, this method should be called. Mainly used to automatically identify whether the entity should be updated or inserted.
 * 3. {@link #afterSave()} When an entity is persisted, this method should be called. Used to reset the entity to normal state.
 */
public abstract class BaseEntity<Id extends Comparable<Id> & Serializable> extends StatefulEntity
        implements Entity<Id> {

    /**
     * The primary key property of the entity. Cannot be a primitive type, can only be a wrapper type or String type.
     * entity id
     *
     * @return entity id
     */
    public abstract Id getId();

    /**
     * Hook after loading from database.
     * When an entity is loaded from the database, this method should be called.
     * This method will mark the entity as persistent state, for automatically identifying whether the entity should be updated or inserted.
     * If it is a newly instantiated instance, do NOT call this method, otherwise it will cause insert failure.
     */
    public final void afterLoad() {
        markPersistent();
        onAfterLoad();
    }

    /**
     * Hook for subclass use after loading.
     */
    protected void onAfterLoad() {

    }

    /**
     * Before an entity is persisted, this method should be called.
     */
    public final void beforeSave() {
        autoChangedStatus();
        this.onBeforeSave();
    }

    /**
     * Hook for subclass use before persisting.
     */
    protected void onBeforeSave() {
    }

    /**
     * After an entity is persisted, this method should be called.
     */
    public final void afterSave() {
        // Has been persisted once, definitely persistent. If delete operation was executed, cannot be saved again.
        markPersistent();
        this.statusRef.set(DbStatus.NORMAL);
        this.modifiedColumns.clear();
        this.saveAll.compareAndSet(true, false);
        onAfterSave();
    }

    /**
     * Hook for subclass use after persisting.
     */
    protected void onAfterSave() {

    }

    @Override
    public int hashCode() {
        Id id = getId();
        return id != null ? id.hashCode() : System.identityHashCode(this);
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
        Id id = getId();
        Object otherId = other.getId();
        if (id == null || otherId == null) {
            return false;
        }
        return id.equals(otherId);
    }

}

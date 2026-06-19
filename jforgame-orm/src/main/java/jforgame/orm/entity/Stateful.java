package jforgame.orm.entity;

import jforgame.orm.core.DbStatus;

/**
 * Object with various db states.
 */
public abstract class Stateful {

    /**
     * Current db status of the entity object.
     */
    protected DbStatus status = DbStatus.NORMAL;

    /**
     * Return the current db status.
     *
     * @return current db status
     */
    public abstract DbStatus getStatus();

    /**
     * Check if entity is in normal state, no need to update to database.
     *
     * @return true if entity status is normal
     */
    public abstract boolean isNormal();

    /**
     * Check if entity is in new state (to be inserted into database).
     *
     * @return true if entity status is new
     */
    public abstract boolean isNew();

    /**
     * Check if entity is in modified state.
     *
     * @return true if entity status is modified
     */
    public abstract boolean isModified();

    /**
     * Check if entity is in logically deleted state (marked for deletion but not yet physically deleted).
     *
     * @return true if entity has been logically deleted
     */
    public abstract boolean isSoftDeleted();

    /**
     * Mark entity as new state (ready to insert into database).
     */
    public abstract void markAsNew();

    /**
     * Mark entity as modified state (ready to update to database).
     */
    public abstract void markAsModified();

    /**
     * Mark entity as logically deleted state (ready to physically delete from database).
     */
    public abstract void markAsSoftDeleted();


}

package jforgame.orm.entity;

import jforgame.orm.core.DbStatus;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Database entity object with various db states.
 */
public abstract class StatefulEntity extends Stateful {

    /**
     * Whether it has been persisted.
     */
    private final AtomicBoolean persistent = new AtomicBoolean(false);

    /**
     * List of fields that need to be persisted in this operation (incremental update).
     */
    protected Set<String> modifiedColumns = new HashSet<>();

    /**
     * Whether all fields need to be saved.
     */
    protected AtomicBoolean saveAll = new AtomicBoolean();

    /**
     * Current db status of the entity object - using AtomicReference instead of volatile.
     */
    protected AtomicReference<DbStatus> statusRef = new AtomicReference<>(DbStatus.NORMAL);

    public Set<String> getAllModifiedColumns() {
        return modifiedColumns;
    }

    /**
     * Add fields that need to be updated in incremental mode.
     * Only applies to entities with Status#UPDATE state.
     * Note: When using this method, make sure the parameters exactly match the database table field names, otherwise data may not be saved completely.
     * @param column Field name that needs to be updated. Must match the database table field name exactly.
     */
    public void addModifiedColumn(String... column) {
        if (column == null || column.length == 0) {
            return;
        }
        modifiedColumns.addAll(Arrays.asList(column));
    }

    /**
     * Force save all fields, e.g., when player logs out. It is recommended to save all fields for safety.
     */
    public void forceSaveAll() {
        saveAll.compareAndSet(false, true);
    }

    /**
     * Whether all fields need to be saved.
     * When saveAll is true or modifiedColumns is empty, all fields need to be saved.
     * @return whether all fields need to be saved
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
        // Use CAS to set INSERT state
        this.statusRef.set(DbStatus.INSERT);
    }

    @Override
    public final void markAsModified() {
        // Only NORMAL state can change to UPDATE
        this.statusRef.compareAndSet(DbStatus.NORMAL, DbStatus.UPDATE);
    }

    @Override
    public final void markAsSoftDeleted() {
        // If current is INSERT state, set to NORMAL
        // Otherwise set to DELETE state
        this.statusRef.updateAndGet(currentStatus -> {
            if (currentStatus == DbStatus.INSERT) {
                return DbStatus.NORMAL;
            } else {
                return DbStatus.DELETE;
            }
        });
    }

    /**
     * Mark as already persisted.
     * When an entity is loaded from the database, it means this entity already exists in the database.
     */
    protected void markPersistent() {
        persistent.compareAndSet(false, true);
    }

    /**
     * Whether there is a corresponding entity in the database.
     *
     * @return whether there is a corresponding entity in the database.
     */
    public boolean existedInDb() {
        return persistent.get();
    }

    /**
     * Auto change db status.
     */
    public void autoChangedStatus() {
        // Delete status can only be set manually
        if (!isSoftDeleted()) {
            // If it already exists in the database, it means modify record
            if (existedInDb()) {
                markAsModified();
            } else {
                markAsNew();
            }
        }
    }

}

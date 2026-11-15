package jforgame.commons.persist.dlq;

import jforgame.commons.persist.Entity;

/**
 * Dead letter entry, stores entity and failure metadata
 * <p>
 * When an entity fails to persist beyond the configured max retry count,
 * it is wrapped in a DeadLetter and stored in the dead letter queue.
 * Developers can inspect, reprocess, or remove dead letters via {@link DeadLetterQueue}.
 */
public class DeadLetter {

    /** Entity unique key, format: ClassName@Id */
    private final String key;
    /** The entity object that failed to persist, may be updated with latest snapshot */
    private Entity<?> entity;
    /** Number of retries before being moved to dead letter queue */
    private final int retryCount;
    /** Timestamp (epoch millis) when this dead letter was created */
    private final long deadAt;
    /** Class name of the entity (for quick diagnosis without deserialization) */
    private final String entityClassName;
    /** Last error message that caused the failure */
    private final String lastErrorMessage;

    public DeadLetter(String key, Entity<?> entity, int retryCount, String lastErrorMessage) {
        this.key = key;
        this.entity = entity;
        this.retryCount = retryCount;
        this.deadAt = System.currentTimeMillis();
        this.entityClassName = entity != null ? entity.getClass().getName() : "unknown";
        this.lastErrorMessage = lastErrorMessage;
    }

    public String getKey() {
        return key;
    }

    public Entity<?> getEntity() {
        return entity;
    }

    /**
     * Update the entity snapshot in this dead letter, preserving all other metadata.
     * Called when a newer version of the entity is received while the key is already dead.
     *
     * @param entity the latest entity state
     */
    public void setEntity(Entity<?> entity) {
        this.entity = entity;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public long getDeadAt() {
        return deadAt;
    }

    public String getEntityClassName() {
        return entityClassName;
    }

    public String getLastErrorMessage() {
        return lastErrorMessage;
    }

    @Override
    public String toString() {
        return String.format(
                "DeadLetter{key='%s', entityClass='%s', retryCount=%d, deadAt=%d, lastError='%s'}",
                key, entityClassName, retryCount, deadAt, lastErrorMessage
        );
    }
}

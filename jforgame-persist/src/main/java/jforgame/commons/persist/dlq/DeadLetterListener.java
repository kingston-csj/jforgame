package jforgame.commons.persist.dlq;

/**
 * Listener for dead letter events
 * <p>
 * When an entity fails to persist beyond the configured max retry count,
 * it is moved to the dead letter queue. Implementations of this interface
 * will be notified so that developers can take action (alerting, manual
 * intervention, metrics collection, etc.).
 *
 * @see DeadLetterQueue#addListener(DeadLetterListener)
 */
public interface DeadLetterListener {

    /**
     * Called when an entity is moved to the dead letter queue
     *
     * @param deadLetter the dead letter entry containing the failed entity and metadata
     */
    void onDeadLetter(DeadLetter deadLetter);

    /**
     * Called when a dead letter is reprocessed (successfully or not)
     *
     * @param deadLetter the dead letter that was reprocessed
     * @param success    true if the reprocess succeeded, false otherwise
     */
    default void onDeadLetterReprocessed(DeadLetter deadLetter, boolean success) {
        // no-op by default
    }

}

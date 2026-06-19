package jforgame.commons.persist;

/**
 * Data persistence service
 */
public interface DbService {

    /**
     * Persist data entity to database
     * This interface will ensure whether table record exists in database
     * If exists, execute update operation, otherwise execute insert action
     *
     * @param entity entity to be persisted
     */
    void saveToDb(Entity<?> entity);

    /**
     * Delete data (game business generally only updates, does not delete, this interface has few usage scenarios)
     *
     * @param entity entity to be deleted
     */
    void deleteFromDb(Entity<?> entity);

    /**
     * Close service
     * If asynchronous persistence, need to save all cached data to database
     */
    void shutDown();

}

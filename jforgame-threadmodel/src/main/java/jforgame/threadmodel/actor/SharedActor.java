package jforgame.threadmodel.actor;

/**
 * Shared Actor, a virtual Actor component for entities without logical subjects, such as login queues
 */
public class SharedActor {

    /**
     * A group composed of several Actors
     */
    private final Actor[] group;

    public SharedActor(Actor[] group) {
        this.group = group;
    }


    /**
     * Get shared Actor based on key
     *
     * @param key shared Actor key
     * @return shared Actor
     */
    public Actor getSharedActor(long key) {
        // Math.abs + cast has edge case issues (abs(Integer.MIN_VALUE) is still negative)
        // So using floorMod here instead of modulo operation
        int index = Math.floorMod(Long.hashCode(key), group.length);
        return group[index];
    }

    /**
     * Get worker thread count
     *
     * @return worker thread count
     */
    public int getWorkerSize() {
        return group.length;
    }

    /**
     * Get mailbox group
     *
     * @return mailbox group
     */
    public Actor[] getGroup() {
        return group;
    }

}

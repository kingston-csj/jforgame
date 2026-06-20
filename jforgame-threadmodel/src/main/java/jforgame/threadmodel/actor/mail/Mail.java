package jforgame.threadmodel.actor.mail;

import jforgame.threadmodel.BaseTask;
import jforgame.threadmodel.actor.Actor;

/**
 * Mail abstract base class
 * All messages delivered to Actor mailbox should extend this class
 */
public abstract class Mail extends BaseTask {

    /**
     * Mail creation time
     */
    protected final long createdTime;

    /**
     * Sender (optional, for tracing)
     */
    protected Actor sender;

    /**
     * Receiver (optional, for tracing)
     */
    protected Actor receiver;

    public Mail() {
        this.createdTime = System.currentTimeMillis();
    }

    /**
     * Mail processing logic, subclasses must implement
     * Responsible for executing specific business logic
     */
    @Override
    public abstract void action();


    /**
     * Set sender
     *
     * @param sender sender
     */
    public void setSender(Actor sender) {
        this.sender = sender;
    }

    /**
     * Get sender
     *
     * @return sender
     */
    public Actor getSender() {
        return sender;
    }

    /**
     * Set receiver
     *
     * @param receiver receiver
     */
    public void setReceiver(Actor receiver) {
        this.receiver = receiver;
    }

    /**
     * Get receiver
     *
     * @return receiver
     */
    public Actor getReceiver() {
        return receiver;
    }

    /**
     * Get mail creation time
     *
     * @return mail creation time
     */
    public long getCreatedTime() {
        return createdTime;
    }


}

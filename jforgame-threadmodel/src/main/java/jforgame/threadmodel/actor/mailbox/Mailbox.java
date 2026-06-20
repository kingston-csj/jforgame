package jforgame.threadmodel.actor.mailbox;


import jforgame.threadmodel.actor.ActorSystem;
import jforgame.threadmodel.actor.mail.Mail;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Mailbox
 * The primary task queue is the thread pool {@link ActorSystem#threadPool}
 * In the actor model, a mailbox is a secondary queue. When each task in {@link ActorSystem#threadPool} is executed,
 * the mailbox's tasks are executed sequentially.
 * The same actor's mailbox is absolutely never executed by multiple threads simultaneously, ensuring thread safety.
 * Note that the same actor's mailbox is only executed by one thread at a time, but at different times,
 * it may be executed by different threads.
 */
public class Mailbox {
    /**
     * Task queue in the mailbox
     */
    protected BlockingQueue<Mail> mails;

    /**
     * Create a mailbox with specified size
     *
     * @param size mailbox size
     */
    public Mailbox(int size) {
        this.mails = new ArrayBlockingQueue<>(size);
    }


    /**
     * Create a mailbox with custom task queue
     *
     * @param mails task queue
     */
    public Mailbox(BlockingQueue<Mail> mails) {
        this.mails = mails;
    }

    /**
     * Receive a mail
     *
     * @param mail mail
     */
    public void receive(Mail mail) {
        if (!this.mails.offer(mail)) {
            throw new IllegalStateException("mail box queue is full");
        }
    }

    /**
     * Get current mail count
     *
     * @return current mail count
     */
    public int getTaskSize() {
        return mails.size();
    }

    /**
     * Check if mailbox is empty
     *
     * @return whether it is empty
     */
    public boolean isEmpty() {
        return mails.isEmpty();
    }

    /**
     * Take a mail from the mailbox
     *
     * @return mail
     */
    public Mail poll() {
        return mails.poll();
    }

    /**
     * Clear the mailbox
     */
    public void clear() {
        mails.clear();
    }

}

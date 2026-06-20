package jforgame.threadmodel.actor;

import jforgame.threadmodel.actor.mail.Mail;
import jforgame.threadmodel.actor.mailbox.Mailbox;

import java.util.Objects;

/**
 * Actor abstract model, an Actor represents an object with a mailbox for receiving messages.
 */
public interface Actor extends Runnable {

    /**
     * Get the bound mailbox
     *
     * @return mailbox
     */
    Mailbox getMailbox();

    /**
     * Send a message to this Actor
     *
     * @param message mail message
     */
    default void tell(Mail message) {
        tell(message, null);
    }

    /**
     * Send a message to this Actor
     *
     * @param message message
     * @param sender sender
     */
    default void tell(Mail message, Actor sender) {
        Objects.requireNonNull(message);
        message.setSender(sender);
        Mailbox mailBox = getMailbox();
        if (mailBox != null) {
            mailBox.receive(message);
        }
    }


    /**
     * Get the Actor model name, e.g., player, monster, guild, etc.
     *
     * @return Actor model name
     */
    default String getModel() {
        return getClass().getSimpleName();
    }

}

package jforgame.threadmodel.actor;

import jforgame.threadmodel.actor.mail.Mail;
import jforgame.threadmodel.actor.mailbox.Mailbox;

import java.util.Objects;

/**
 * Actor抽象模型, 一个Actor代表一个对象，拥有一个邮箱，用于接收消息。
 */
public interface Actor extends Runnable {

    /**
     * 绑定的邮箱
     * @return 邮箱
     */
    Mailbox getMailbox();

    /**
     * 发送消息到当前Actor
     *
     * @param message 邮件消息
     */
    default void tell(Mail message) {
        tell(message, null);
    }

    /**
     * 发送消息到当前Actor
     *
     * @param message 消息
     * @param sender  发送者
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
     * 获取Actor模型名称，例如player, monster, guild等
     * @return Actor模型名称
     */
    default String getModel() {
        return getClass().getSimpleName();
    }

}

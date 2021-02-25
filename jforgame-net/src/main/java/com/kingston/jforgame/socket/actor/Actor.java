package com.kingston.jforgame.socket.actor;

/**
 * Actor抽象模型
 *
 * @author kingston
 */
public interface Actor {

    /**
     * 绑定的邮箱
     */
    MailBox mailBox();

    default void tell(Runnable message) {
        MailBox mailBox = mailBox();
        mailBox.receive(message);
    }

}

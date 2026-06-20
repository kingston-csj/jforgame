package jforgame.socket.core.protocol.message;

import jforgame.socket.core.protocol.annotation.MessageMeta;

/**
 * Base communication message interface.
 * This interface is optional, you can use your own way to collect all message classes and bind their cmd.
 * @see MessageFactory
 */
public interface Message {

    /**
     * Message module, each message is bound to a business module, which can greatly improve the clarity of business logic
     * and facilitate aspect control for modules, such as feature switches.
     * @return message module
     */
    default short getModule() {
        MessageMeta annotation = getClass().getAnnotation(MessageMeta.class);
        if (annotation != null) {
            return annotation.module();
        }
        return 0;
    }

    /**
     * Message cmd, each message has a unique cmd for identifying message types in communication.
     * Message types cannot be duplicated.
     * Each message will be bound to a unique method executor.
     * @return message cmd
     */
    default int getCmd() {
        MessageMeta annotation = getClass().getAnnotation(MessageMeta.class);
        if (annotation != null) {
            return annotation.cmd();
        }
        return 0;
    }


    default String key() {
        return this.getModule() + "_" + this.getCmd();
    }

}

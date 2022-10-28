package jforgame.socket.message;

import jforgame.socket.annotation.MessageMeta;

public interface Message {



    /**
     * messageMeta, module of message
     *
     * @return
     */
    default short getModule() {
        MessageMeta annotation = getClass().getAnnotation(MessageMeta.class);
        if (annotation != null) {
            return annotation.module();
        }
        return 0;
    }

    /**
     * messageMeta, subType of module
     * @see
     * @return
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

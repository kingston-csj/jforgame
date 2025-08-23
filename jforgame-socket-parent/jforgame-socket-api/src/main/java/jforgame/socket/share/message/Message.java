package jforgame.socket.share.message;

import jforgame.socket.share.annotation.MessageMeta;

/**
 * 通信消息基础接口
 * 该接口是可选的，你可以使用自己的方式来收集所有的消息类并绑定其cmd
 * @see MessageFactory
 */
public interface Message {

    /**
     * 消息模块，每一个消息绑定一个业务模块，可以极大提高业务逻辑的清晰度，也可以方便对模块作切面控制，例如：功能开关
     * @return 消息模块
     */
    default short getModule() {
        MessageMeta annotation = getClass().getAnnotation(MessageMeta.class);
        if (annotation != null) {
            return annotation.module();
        }
        return 0;
    }

    /**
     * 消息cmd，每个消息都有一个唯一的cmd，用于在通信中识别消息类型
     * 消息类型不可重复
     * 每一个消息都会绑定到一个唯一的方法执行者
     * @return 消息cmd
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

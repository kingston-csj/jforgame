package jforgame.server.cross.core.callback;

import jforgame.socket.message.Message;

public interface RequestCallback {

    /**
     * 请求方接受回调消息的业务处理
     *
     * @param callBack
     */
    void onSuccess(Message callBack);

    void onError(Throwable error);

}

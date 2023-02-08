package jforgame.server.cross.core.callback;

import jforgame.socket.share.message.Message;

public interface RequestCallback {

    /**
     * 请求方接受回调消息的业务处理
     *
     * @param callBack
     */
    void onSuccess(Object callBack);

    void onError(Throwable error);

}

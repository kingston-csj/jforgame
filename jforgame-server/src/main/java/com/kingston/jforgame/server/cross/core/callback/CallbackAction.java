package com.kingston.jforgame.server.cross.core.callback;

import com.kingston.jforgame.socket.message.Message;

public abstract class CallbackAction {

    public abstract void onMessageReceive(Message callBack);

    public abstract void onError();
}

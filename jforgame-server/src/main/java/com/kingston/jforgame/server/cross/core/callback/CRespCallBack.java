package com.kingston.jforgame.server.cross.core.callback;

import com.kingston.jforgame.server.cross.core.CrossCommands;
import com.kingston.jforgame.server.game.Modules;
import com.kingston.jforgame.server.logs.LoggerUtils;
import com.kingston.jforgame.server.utils.JsonUtils;
import com.kingston.jforgame.socket.annotation.MessageMeta;
import com.kingston.jforgame.socket.message.Message;

@MessageMeta(module = Modules.LADDER, cmd = CrossCommands.C2G_CALL_BACK)
public class CRespCallBack extends Message {

    private int index;

    private int rpc;

    private String data;

    private String msgClass;

    public static CRespCallBack valueOf(Message message) {
        CRespCallBack response = new CRespCallBack();
        response.data = JsonUtils.object2String(message);
        response.msgClass = message.getClass().getName();

        return response;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getMsgClass() {
        return msgClass;
    }

    public void setMsgClass(String msgClass) {
        this.msgClass = msgClass;
    }

    public int getRpc() {
        return rpc;
    }

    public void setRpc(int rpc) {
        this.rpc = rpc;
    }

    public Message getMessage() {
        try {
            return (Message) JsonUtils.string2Object(data, Class.forName(msgClass));
        } catch (Exception e) {
            LoggerUtils.error("", e);
            return null;
        }
    }
}
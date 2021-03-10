package jforgame.server.cross.core.callback;

import jforgame.server.cross.core.CrossCommands;
import jforgame.server.game.Modules;
import jforgame.server.logs.LoggerUtils;
import jforgame.server.utils.JsonUtils;
import jforgame.socket.annotation.MessageMeta;
import jforgame.socket.message.Message;

/**
 * 跨服回调响应方
 */
@MessageMeta(module = Modules.CROSS, cmd = CrossCommands.F2G_CALL_BACK)
public class F2GCallBack extends Message {

    private int index;

    private int rpc;

    private String data;

    private String msgClass;

    public static F2GCallBack valueOf(Message message) {
        F2GCallBack response = new F2GCallBack();
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
package jforgame.demo.cross.core;

import jforgame.commons.util.JsonUtil;
import jforgame.demo.game.Modules;
import jforgame.socket.share.annotation.MessageMeta;
import jforgame.socket.share.message.Message;

/**
 * 跨服回调响应方
 */
@MessageMeta(module = Modules.CROSS, cmd = CrossCommands.F2G_CALL_BACK)
public class F2GCallBack implements Message {

    private String data;

    private String msgClass;

    public static F2GCallBack valueOf(Message message) {
        F2GCallBack response = new F2GCallBack();
        response.data = JsonUtil.object2String(message);
        response.msgClass = message.getClass().getName();

        return response;
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

    public Message getMessage() {
        try {
            return (Message) JsonUtil.string2Object(data, Class.forName(msgClass));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
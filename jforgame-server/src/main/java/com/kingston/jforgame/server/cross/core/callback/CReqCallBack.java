package com.kingston.jforgame.server.cross.core.callback;

import com.kingston.jforgame.server.cross.core.CrossCommands;
import com.kingston.jforgame.server.utils.JsonUtils;
import com.kingston.jforgame.socket.annotation.MessageMeta;
import com.kingston.jforgame.socket.message.Message;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@MessageMeta(module = CrossCommands.G2C_CALL_BACK)
public class CReqCallBack extends Message {

    private int index;

    private int type;

    private transient Map<String, String> params = new HashMap<>();

    private String data;

    public void addParam(String key, String value) {
        this.params.put(key, value);
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void serialize() {
        String json = JsonUtils.object2String(params);
        this.data = Base64.getEncoder().encodeToString(json.getBytes());
    }

    public void deserialize() {
        byte[] json = Base64.getDecoder().decode(this.data);
        this.params = JsonUtils.string2Map(new String(json), String.class, String.class);
    }


    public Map<String, String> getParams() {
        return params;
    }
}
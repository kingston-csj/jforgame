package jforgame.demo.cross.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jforgame.commons.JsonUtil;
import jforgame.demo.game.Modules;
import jforgame.socket.client.RequestResponseFuture;
import jforgame.socket.client.Traceable;
import jforgame.socket.share.annotation.MessageMeta;
import jforgame.socket.share.message.Message;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 跨服回调请求方
 */
@MessageMeta(module = Modules.CROSS, cmd = CrossCommands.G2F_CALL_BACK)
public class G2FCallBack implements Message, Traceable {

    /**
     * 关联id {@link RequestResponseFuture#getCorrelationId()}
     */
    private int index;

    /**
     * 子类型 {@link CallBackCommands#HELLO}
     */
    private int command;

    @JsonIgnore
    private transient Map<String, String> params = new HashMap<>();

    private String data;

    private AtomicInteger idFactory = new AtomicInteger();

    public G2FCallBack() {
        int index = idFactory.getAndIncrement();
        setIndex(index);
    }

    public void addParam(String key, String value) {
        this.params.put(key, value);
    }

    public void serialize() {
        String json = JsonUtil.object2String(params);
        this.data = Base64.getEncoder().encodeToString(json.getBytes());
    }

    public void deserialize() {
        byte[] json = Base64.getDecoder().decode(this.data);
        this.params = JsonUtil.string2Map(new String(json), String.class, String.class);
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getCommand() {
        return command;
    }

    public void setCommand(int command) {
        this.command = command;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Map<String, String> getParams() {
        return params;
    }

}
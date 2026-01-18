package jforgame.demo.tools.protocol.diy;

import java.lang.reflect.Field;

public class ProtocolFieldDoc {

    private Field meta;

    private String name;

    private String type;

    private String desc;

    public Field getMeta() {
        return meta;
    }

    public void setMeta(Field meta) {
        this.meta = meta;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}

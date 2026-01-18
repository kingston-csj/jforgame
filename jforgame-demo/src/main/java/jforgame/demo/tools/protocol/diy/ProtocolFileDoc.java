package jforgame.demo.tools.protocol.diy;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class ProtocolFileDoc {

    private Class<?> clazz;

    /**
     * 类名称
     */
    private String name;

    /**
     * 协议号
     */
    private String cmd;

    /**
     * 协议注释
     */
    private String desc;

    /**
     * 所有字段
     */
    private List<ProtocolFieldDoc> fields = new LinkedList<>();

    private Set<String> importItems = new LinkedHashSet<>();

    private Set<String> importItems2 = new LinkedHashSet<>();

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public List<ProtocolFieldDoc> getFields() {
        return fields;
    }

    public void setFields(List<ProtocolFieldDoc> fields) {
        this.fields = fields;
    }

    public Set<String> getImportItems() {
        return importItems;
    }

    public void setImportItems(Set<String> importItems) {
        this.importItems = importItems;
    }

    public Set<String> getImportItems2() {
        return importItems2;
    }

    public void setImportItems2(Set<String> importItems2) {
        this.importItems2 = importItems2;
    }
}

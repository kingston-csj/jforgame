package jforgame.data.common;

import jforgame.data.annotation.DataTable;
import jforgame.data.annotation.Id;


/**
 * 通用常量表
 * 如果配置表名字不叫"common"，可修改application.yml中 jforgame.data.commonTableName属性值
 */
@DataTable(name = "common")
public class CommonData {

    @Id
    private int id;

    private String key;

    private String value;

    private String desc;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}

package jforgame.demo.game.database.config.bean;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class ConfigActivity {

    @Column
    @Id
    private Integer id;
    /**
     * 活动类型
     */
    @Column
    private int type;
    /**
     * 活动名称
     */
    @Column
    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}

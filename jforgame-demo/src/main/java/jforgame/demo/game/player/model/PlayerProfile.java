package jforgame.demo.game.player.model;

public class PlayerProfile {

    private long id;

    private String name;

    private int level;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public String toString() {
        return "PlayerBaseInfo [id=" + id + ", name=" + name + ", level=" + level + "]";
    }


}

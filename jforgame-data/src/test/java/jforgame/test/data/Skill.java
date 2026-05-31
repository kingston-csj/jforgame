package jforgame.test.data;

public class Skill {

    private int id;

    private int skillId;

    private int level;

    private ConsumeDef[] consumes;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSkillId() {
        return skillId;
    }

    public void setSkillId(int skillId) {
        this.skillId = skillId;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public ConsumeDef[] getConsumes() {
        return consumes;
    }

    public void setConsumes(ConsumeDef[] consumes) {
        this.consumes = consumes;
    }
}

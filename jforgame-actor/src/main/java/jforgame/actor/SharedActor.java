package jforgame.actor;

/**
 * 共享Actor
 */
public class SharedActor {

    /**
     * 由若干个Actor组成一个集体
     */
    private final Actor[] group;

    public SharedActor(Actor[] group) {
        this.group = group;
    }


    /**
     * 共享邮箱
     *
     * @param key
     * @return
     */
    public Actor getSharedActor(long key) {
        int index = Math.abs((int) (key % group.length));
        return group[index];
    }

    /**
     * 获取工作线程数量
     */
    public int getWorkerSize() {
        return group.length;
    }

    /**
     * 获取邮箱组
     */
    public Actor[] getGroup() {
        return group;
    }

}

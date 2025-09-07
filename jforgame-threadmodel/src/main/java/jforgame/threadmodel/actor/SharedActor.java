package jforgame.threadmodel.actor;

/**
 * 共享Actor，对于一些无逻辑主体，例如登录队列，所提供的虚拟Actor组件
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
     * 根据key获取共享Actor
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

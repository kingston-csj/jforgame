package jforgame.server.thread;

import jforgame.socket.actor.MailBox;

public class CommonMailGroup {

    private String name;

    private int workerSize;

    private MailBox[] mailGroup;

    public CommonMailGroup(String name, int workerSize) {
        this.name = name;
        this.workerSize = workerSize;
        this.initWorkers();
    }

    private void initWorkers() {
        mailGroup = new MailBox[workerSize];
        for (int i = 0; i < workerSize; i++) {
            mailGroup[i] = ThreadCenter.createBusinessMailBox(name);
        }
    }

    /**
     * 共享邮箱
     * @param key
     * @return
     */
    public MailBox getSharedMailQueue(long key){
        int index = (int) (key % workerSize);
        return mailGroup[index];
    }

}

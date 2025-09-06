package jforgame.actor;


import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * 邮箱
 * 一级任务队列为线程池 {@link ActorSystem#pool}
 * actor模型里的邮箱, 邮箱相当于一个二级队列， 当{@link ActorSystem#pool}的每一个任务被执行时，该邮箱的任务会按顺序串行执行
 * 绝对不存在同一个actor的邮箱被多个线程同时执行，保证了线程安全
 * 需要注意的是，同一个actor的邮箱在同一时刻只会被一个线程执行，但在不同时刻，有可能在不同的线程执行
 */
public class Mailbox {

    BlockingQueue<Mail> mails;

    public Mailbox() {
        this.mails = new ArrayBlockingQueue<>(512);
    }

    public void receive(Mail mail) {
        if (!this.mails.offer(mail)) {
            throw new IllegalStateException("mail box queue is full");
        }
    }

    /**
     * 获取当前邮件数量
     */
    public int getTaskSize() {
        return mails.size();
    }

}
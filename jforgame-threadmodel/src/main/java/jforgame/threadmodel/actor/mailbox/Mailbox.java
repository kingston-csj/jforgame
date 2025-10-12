package jforgame.threadmodel.actor.mailbox;


import jforgame.threadmodel.actor.ActorSystem;
import jforgame.threadmodel.actor.mail.Mail;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * 邮箱
 * 一级任务队列为线程池 {@link ActorSystem#threadPool}
 * actor模型里的邮箱, 邮箱相当于一个二级队列， 当{@link ActorSystem#threadPool}的每一个任务被执行时，该邮箱的任务会按顺序串行执行
 * 绝对不存在同一个actor的邮箱被多个线程同时执行，保证了线程安全
 * 需要注意的是，同一个actor的邮箱在同一时刻只会被一个线程执行，但在不同时刻，有可能在不同的线程执行
 */
public class Mailbox {
    /**
     * 邮箱中的任务队列
     */
    protected BlockingQueue<Mail> mails;

    /**
     * 创建一个指定大小的邮箱
     *
     * @param size 邮箱大小
     */
    public Mailbox(int size) {
        this.mails = new ArrayBlockingQueue<>(size);
    }


    /**
     * 创建一个自定义任务队列的邮箱
     *
     * @param mails 任务队列
     */
    public Mailbox(BlockingQueue<Mail> mails) {
        this.mails = mails;
    }

    /**
     * 接收一封邮件
     *
     * @param mail 邮件
     */
    public void receive(Mail mail) {
        if (!this.mails.offer(mail)) {
            throw new IllegalStateException("mail box queue is full");
        }
    }

    /**
     * 获取当前邮件数量
     *
     * @return 当前邮件数量
     */
    public int getTaskSize() {
        return mails.size();
    }

    /**
     * 判断邮箱是否为空
     *
     * @return 是否为空
     */
    public boolean isEmpty() {
        return mails.isEmpty();
    }

    /**
     * 从邮箱中取出一封邮件
     *
     * @return 邮件
     */
    public Mail poll() {
        return mails.poll();
    }

}
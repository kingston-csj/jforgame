package jforgame.server.tools;

import jforgame.socket.message.Message;

public class MessageMetadata implements Comparable<MessageMetadata> {

    private String name;

    private short module;

    private byte cmd;

    private int id;

    public static MessageMetadata valueOf(Message message) {
        MessageMetadata metadata = new MessageMetadata();
        metadata.module  = message.getModule();
        metadata.cmd     = message.getCmd();
        metadata.name    = message.getClass().getSimpleName();

        boolean negative = message.getCmd() < 0;
        metadata.id = Math.abs(message.getModule()) * 100 + metadata.cmd;
        metadata.id = negative ? -metadata.id : metadata.id;

        return metadata;
    }

    public short getModule() {
        return module;
    }

    public byte getCmd() {
        return cmd;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "{" +
                "name='" + name + '\'' +
                ", module=" + module +
                ", cmd=" + cmd +
                ", id=" + id +
                '}';
    }

    @Override
    public int compareTo(MessageMetadata o) {
        if (getId() > o.getId()) {
            return 1;
        } else if (getId() < o.getId()) {
            return -1;
        }
        return 0;
    }
}

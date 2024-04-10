package jforgame.demo.udp;

import jforgame.socket.share.message.Message;

public class UdpMessage implements Message {

    private String senderIp;

    private int senderPort;

    private String receiverIp;

    private int receiverPort;

    public String getSenderIp() {
        return senderIp;
    }

    public void setSenderIp(String senderIp) {
        this.senderIp = senderIp;
    }

    public int getSenderPort() {
        return senderPort;
    }

    public void setSenderPort(int senderPort) {
        this.senderPort = senderPort;
    }

    public String getReceiverIp() {
        return receiverIp;
    }

    public void setReceiverIp(String receiverIp) {
        this.receiverIp = receiverIp;
    }

    public int getReceiverPort() {
        return receiverPort;
    }

    public void setReceiverPort(int receiverPort) {
        this.receiverPort = receiverPort;
    }
}

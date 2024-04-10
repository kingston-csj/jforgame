package jforgame.demo.udp;

import jforgame.socket.share.annotation.MessageMeta;

@MessageMeta(cmd = 55557)
public class ResWelcome extends UdpMessage {

    private long time;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}

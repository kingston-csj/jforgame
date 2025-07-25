package jforgame.socket.share;

/**
 * socket连接的主机与端口
 */
public class HostAndPort {

    /**
     * ip address
     */
    private String host;

    /**
     * socket port
     */
    private int port;

    public static HostAndPort valueOf(String host, int port) {
        HostAndPort hostPort = new HostAndPort();
        hostPort.host = host;
        hostPort.port = port;

        return hostPort;
    }

    public static HostAndPort valueOf(int port) {
        HostAndPort hostPort = new HostAndPort();
        hostPort.host = "localhost";
        hostPort.port = port;

        return hostPort;
    }


    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}

package jforgame.server.game.cross.ladder.message;

import jforgame.server.ServerConfig;

public class F2M_HeatBeat {

    private String inetIp;

    private int serverId;
    /**
     * 跨服端口
     */
    private int port;

    public F2M_HeatBeat() {
        ServerConfig config = ServerConfig.getInstance();
        this.serverId = config.getServerId();
        this.inetIp = config.getInetAddr();
        this.port = config.getCrossPort();
    }

    public int getServerId() {
        return serverId;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getInetIp() {
        return inetIp;
    }

    public void setInetIp(String inetIp) {
        this.inetIp = inetIp;
    }

}

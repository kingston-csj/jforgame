package jforgame.server.game.database.config.bean;

public class ConfigCross {

    private int id;

    private String ip;

    private String name;

    private int gamePort;

    private int rpcPort;
    /**
     * 对应的跨服serverId
     */
    private int crossServer;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getGamePort() {
        return gamePort;
    }

    public void setGamePort(int gamePort) {
        this.gamePort = gamePort;
    }

    public int getRpcPort() {
        return rpcPort;
    }

    public void setRpcPort(int rpcPort) {
        this.rpcPort = rpcPort;
    }

    public int getCrossServer() {
        return crossServer;
    }

    public void setCrossServer(int crossServer) {
        this.crossServer = crossServer;
    }
}

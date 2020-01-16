package com.kingston.jforgame.merge.config;

import org.simpleframework.xml.Element;

public class MergeServer {

    @Element(required = true)
    private int serverId;
    @Element(required = true)
    private String url;
    @Element(required = true)
    private String user;
    @Element(required = true)
    private String password;

    public int getServerId() {
        return serverId;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "MergeServer{" +
                "serverId=" + serverId +
                ", url='" + url + '\'' +
                ", user='" + user + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}

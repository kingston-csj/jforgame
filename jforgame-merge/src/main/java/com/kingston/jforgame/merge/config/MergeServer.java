package com.kingston.jforgame.merge.config;

import org.simpleframework.xml.Element;

public class MergeServer {

    @Element(required = true)
    private int serverId;
    @Element(required = true)
    private String dbName;
    @Element(required = true)
    private String url;
    @Element(required = true)
    private String user;
    @Element(required = true)
    private String password;

    public int getServerId() {
        return serverId;
    }

    public String getUrl() {
        return url.replace("{0}", dbName);
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String getDbName() {
        return dbName;
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

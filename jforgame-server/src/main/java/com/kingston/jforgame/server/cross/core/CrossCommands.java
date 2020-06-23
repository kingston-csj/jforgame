package com.kingston.jforgame.server.cross.core;

/**
 * 跨服内部点对点协议（协议号全部用负数）
 */
public interface CrossCommands {

    /**
     * 请求--心跳包
     */
    int G2C_HEART_BEAT = -1 ;

    /**
     * 推送--心跳包
     */
    int C2G_HEART_BEAT = -2 ;

    /**
     * 请求--回调
     */
    int G2C_CALL_BACK = -3 ;


    /**
     * 推送--回调
     */
    int C2G_CALL_BACK = -4 ;

    /**
     * 请求--登录跨服
     */
    int G2C_LOGIN_TO_SERVER = -5 ;

    /**
     * 推送--登录跨服
     */
    int C2G_LOGIN_TO_SERVER = -6 ;

    /**
     * 推送--退出跨服
     */
    int G2C_LEAVE_CROSS = -7 ;
}

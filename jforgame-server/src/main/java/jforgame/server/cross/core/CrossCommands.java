package jforgame.server.cross.core;

/**
 * 跨服内部点对点协议(允许用负数)
 */
public interface CrossCommands {

    /**
     * 请求--心跳包
     */
    byte G2F_HEART_BEAT = 1 ;

    /**
     * 推送--心跳包
     */
    byte F2G_HEART_BEAT = 2 ;

    /**
     * 请求--回调
     */
    byte G2F_CALL_BACK = 3 ;

    /**
     * 推送--回调
     */
    byte F2G_CALL_BACK = 4 ;

    /**
     * 请求--登录跨服
     */
    byte G2F_LOGIN_TO_SERVER = 5 ;

    /**
     * 推送--登录跨服
     */
    byte F2G_LOGIN_TO_SERVER = 6 ;

    /**
     * 推送--退出跨服
     */
    byte G2F_LEAVE_CROSS = 7 ;
}

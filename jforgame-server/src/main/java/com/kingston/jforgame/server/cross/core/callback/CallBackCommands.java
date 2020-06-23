package com.kingston.jforgame.server.cross.core.callback;

public interface CallBackCommands {

    int HELLO = 1;

    int LOGIN = 2;

    int LOGOUT = 3;

    /**初始化跨服试道大会参赛名单*/
    int SHIDAO_INIT = 4;

    /**跨服试道大会结果*/
    int SHIDAO_RESULT = 5;

    int GET_SHIDAO_PLAN = 6;

    int GET_SHIDAO_HISTORY = 7;

    int GET_SHIDAO_ZONE_PLAN = 8;

    int GET_SHIDAO_ZONE_INFO = 9;
}
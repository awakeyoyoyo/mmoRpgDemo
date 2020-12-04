package com.liqihao.commons;

public interface ConstantValue {
    //包头
    public static final  int FLAG=6617329;

    //场景模块
    public static final short SCENE_MODULE=1111;
    //请求部分
    //请求可以前往的场景
    public static final short ASK_CAN_REQUEST=1000;
    //请求当前场景的角色
    public static final short FIND_ALL_ROLES_REQUEST=1001;
    //请求前往下一个场景
    public static final short WENT_REQUEST=1002;

    //响应部分
    //请求可以前往的场景的响应
    public static final short ASK_CAN_RESPONSE=1010;
    //请求当前场景的角色的响应
    public static final short FIND_ALL_ROLES_RESPONSE=1011;
    //请求前往下一个场景的响应
    public static final short WENT_RESPONSE=1012;

    //玩家模块
    public static final short PLAY_MODULE=2222;
    //登陆请求
    public static final short LOGIN_REQUEST=2000;
    //注册请求
    public static final short REGISTER_REQUEST=2001;
    //退出登陆请求
    public static final short LOGOUT_REQUEST=2002;

    //response
    //登陆响应
    public static final short LOGIN_RESPONSE=2020;
    //注册响应
    public static final short REGISTER_RESPONSE=2021;
    //退出登陆响应
    public static final short LOGOUT_RESPONSE=2022;

    //游戏系统模块
    public static final short GAME_SYSTEM_MODULE=3333;
    //客户端超时请求
    public static final short NET_IO_OUTTIME=2003;
    //客户端超市响应
    public static final short OUT_RIME_RESPONSE=3000;
}

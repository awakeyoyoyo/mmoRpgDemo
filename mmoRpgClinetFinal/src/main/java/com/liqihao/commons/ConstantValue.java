package com.liqihao.commons;

public interface ConstantValue {
    //包头
    int FLAG=6617329;

    //场景模块
     String SCENE_MODULE="sceneServiceImpl";
    //请求部分
    //请求可以前往的场景
    int ASK_CAN_REQUEST=1000;
    //请求当前场景的角色
    int FIND_ALL_ROLES_REQUEST=1001;
    //请求前往下一个场景
    int WENT_REQUEST=1002;
    //与npc对话
    int TALK_NPC_REQUEST=1003;
    //响应部分
    //请求可以前往的场景的响应
    int ASK_CAN_RESPONSE=1500;
    //请求当前场景的角色的响应
    int FIND_ALL_ROLES_RESPONSE=1501;
    //请求前往下一个场景的响应
    int WENT_RESPONSE=1502;
    //与npc对话响应
    int TALK_NPC_RESPONSE=1503;

    //玩家模块
    String PLAY_MODULE="playServiceImpl";
    //登陆请求
    int LOGIN_REQUEST=2000;
    //注册请求
    int REGISTER_REQUEST=2001;
    //退出登陆请求
    int LOGOUT_REQUEST=2002;

    //response
    //登陆响应
    int LOGIN_RESPONSE=2500;
    //注册响应
    int REGISTER_RESPONSE=2501;
    //退出登陆响应
    int LOGOUT_RESPONSE=2502;

    //游戏系统模块
    String GAME_SYSTEM_MODULE="gameSystemServiceImpl";
    //客户端超时请求
    int NET_IO_OUTTIME=3000;
    //客户端超市响应
    int OUT_RIME_RESPONSE=3500;

    //客户端指令
    //请求可前往的场景
    String ASK_CAN_REQUEST_CMD="askCan";
    //请求当前场景的所有角色
    String FIND_ALL_ROLES_REQUEST_CMD="findAllRoles";
    //请求可前往的
    String WENT_REQUEST_CMD="went";
    //登陆
    String LOGIN_REQUEST_CMD="login";
    //注册
    String REGISTER_REQUEST_CMD="register";
    //退出登陆
    String LOGOUT_REQUEST_CMD="logout";
    //退出登陆
    String TALK_NPC_REQUEST_CMD="talk";
}

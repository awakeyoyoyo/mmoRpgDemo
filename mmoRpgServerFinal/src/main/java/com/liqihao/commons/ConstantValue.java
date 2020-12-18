package com.liqihao.commons;

public interface ConstantValue {
    //包头
    int FLAG=6617329;

    int BAG_MAX_VALUE=99;

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
    int USE_SKILL_REQUEST =2003;
    //response
    //登陆响应
    int LOGIN_RESPONSE=2500;
    //注册响应
    int REGISTER_RESPONSE=2501;
    //退出登陆响应
    int LOGOUT_RESPONSE=2502;
    int USE_SKILL_RSPONSE =2503;
    int DAMAGES_NOTICE_RESPONSE=2504;
    //游戏系统模块
    String GAME_SYSTEM_MODULE="gameSystemServiceImpl";
    //客户端超时请求
    int NET_IO_OUTTIME=3000;
    //客户端超市响应
    int OUT_RIME_RESPONSE=3500;
    //背包模块
    String BAKCPACK_MODULE="backpackServiceImpl";
    int BACKPACK_MSG_REQUEST=4000;
    int USE_REQUEST=4001;
    int ABANDON_REQUEST=4002;
    int ADD_ARTICLE_REQUEST=4003;
    int BACKPACK_MSG_RESPONSE=4500;
    int USE_RESPONSE=4501;
    int ABANDON_RESPONSE=4502;
    int ADD_ARTICLE_RESPONSE=4503;
    //装备模块
    String EQUIPMENT_MODULE="equipmentServiceImpl";
    int ADD_EQUIPMENT_REQUEST=5000;
    int EQUIPMENT_MSG_REQUEST=5001;
    int REDUCE_EQUIPMENT_REQUEST=5002;
    int FIX_EQUIPMENT_REQUEST=5003;
    int ADD_EQUIPMENT_RESPONSE=5500;
    int EQUIPMENT_MSG_RESPONSE=5501;
    int REDUCE_EQUIPMENT_RESPONSE=5502;
    int FIX_EQUIPMENT_RESPONSE=5503;
}

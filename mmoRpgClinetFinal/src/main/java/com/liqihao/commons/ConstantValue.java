package com.liqihao.commons;

public interface ConstantValue {
    //包头
    public static final  int FLAG=6617329;
    //SCENE_MODULE
    public static final short SCENE_MODULE=1111;
    //requset
    public static final short ASK_CAN_REQUEST=1000;
    public static final short FIND_ALL_ROLES_REQUEST=1001;
    public static final short WENT_REQUEST=1002;
    public static final short WHERE_REQUEST=1003;
    //response
    public static final short ASK_CAN_RESPONSE=1010;
    public static final short FIND_ALL_ROLES_RESPONSE=1011;
    public static final short WENT_RESPONSE=1012;
    public static final short WHERE_RESPONSE=1013;
    //PLAY_MODULE
    public static final short PLAY_MODULE=2222;
    //requset
    public static final short LOGIN_REQUEST=2000;
    public static final short REGISTER_REQUEST=2001;
    public static final short LOGOUT_REQUEST=2002;
    //response
    public static final short LOGIN_RESPONSE=2020;
    public static final short REGISTER_RESPONSE=2021;
    public static final short LOGOUT_RESPONSE=2022;
    //GAME_SYSTEM_MODULE
    public static final short GAME_SYSTEM_MODULE=3333;
    //response
    public static final short OUT_RIME_RESPONSE=3000;
    //option
    public static final String ASK_CAN_REQUEST_CMD="askCan";
    public static final String FIND_ALL_ROLES_REQUEST_CMD="findAllRoles";
    public static final String WENT_REQUEST_CMD="went";
    public static final String WHERE_REQUEST_CMD="where";
    public static final String LOGIN_REQUEST_CMD="login";
    public static final String REGISTER_REQUEST_CMD="register";
    public static final String LOGOUT_REQUEST_CMD="logout";
}

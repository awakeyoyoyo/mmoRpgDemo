package com.liqihao.commons;

import com.liqihao.pojo.MmoScene;
import com.liqihao.protobufObject.PlayModel;
import com.liqihao.protobufObject.SceneModel;

public class CacheUtil {
    public static MmoScene nowScene=null;
    public static PlayModel.MmoSimpleRole nowRoles=null;


    public static MmoScene getNowScene() {
        return nowScene;
    }

    public static void setNowScene(MmoScene nowScene) {
        CacheUtil.nowScene = nowScene;
    }

    public static PlayModel.MmoSimpleRole getNowRoles() {
        return nowRoles;
    }

    public static void setNowRoles(PlayModel.MmoSimpleRole nowRoles) {
        CacheUtil.nowRoles = nowRoles;
    }
}

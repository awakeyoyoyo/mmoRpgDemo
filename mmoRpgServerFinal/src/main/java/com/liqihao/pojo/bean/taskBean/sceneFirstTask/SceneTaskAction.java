package com.liqihao.pojo.bean.taskBean.sceneFirstTask;

import com.liqihao.pojo.bean.taskBean.BaseTaskAction;

/**
 * @Classname SceneTaskAction
 * @Description 第一次进入场景事件
 * @Author lqhao
 * @Date 2021/1/26 11:06
 * @Version 1.0
 */
public class SceneTaskAction extends BaseTaskAction {
    private Integer sceneId;
    public Integer getSceneId() {
        return sceneId;
    }
    public void setSceneId(Integer sceneId) {
        this.sceneId = sceneId;
    }
}

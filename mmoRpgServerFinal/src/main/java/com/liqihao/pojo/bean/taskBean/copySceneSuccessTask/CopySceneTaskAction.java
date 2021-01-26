package com.liqihao.pojo.bean.taskBean.copySceneSuccessTask;

import com.liqihao.pojo.bean.taskBean.BaseTaskAction;

/**
 * @Classname CopySceneTaskAction
 * @Description 副本通关动作
 * @Author lqhao
 * @Date 2021/1/26 10:54
 * @Version 1.0
 */
public class CopySceneTaskAction extends BaseTaskAction {
    private Integer copySceneId;

    public Integer getCopySceneId() {
        return copySceneId;
    }

    public void setCopySceneId(Integer copySceneId) {
        this.copySceneId = copySceneId;
    }
}

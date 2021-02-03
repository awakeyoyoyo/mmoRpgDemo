package com.liqihao.pojo.bean.taskBean.equipmentLevelTask;

import com.liqihao.pojo.bean.taskBean.BaseTaskAction;

/**
 * @Classname EquipmentTaskAction
 * @Description 改变装备等级动作
 * @Author lqhao
 * @Date 2021/1/27 15:18
 * @Version 1.0
 */
public class EquipmentTaskLevelAction extends BaseTaskAction {
    private Integer equipmentLevel;

    public Integer getChangeLevel() {
        return equipmentLevel;
    }

    public void setChangeLevel(Integer changeLevel) {
        this.equipmentLevel = changeLevel;
    }
}

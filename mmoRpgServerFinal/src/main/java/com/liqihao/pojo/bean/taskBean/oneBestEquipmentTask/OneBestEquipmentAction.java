package com.liqihao.pojo.bean.taskBean.oneBestEquipmentTask;

import com.liqihao.pojo.bean.taskBean.BaseTaskAction;

/**
 * @Classname OneBestEquipmentAction
 * @Description 拥有五星以上
 * @Author lqhao
 * @Date 2021/1/27 15:27
 * @Version 1.0
 */
public class OneBestEquipmentAction extends BaseTaskAction {
    Integer equipmentLevel;

    public Integer getEquipmentLevel() {
        return equipmentLevel;
    }

    public void setEquipmentLevel(Integer equipmentLevel) {
        this.equipmentLevel = equipmentLevel;
    }
}

package com.liqihao.pojo.bean.taskBean.oneBestEquipmentTask;

import com.liqihao.Cache.TaskMessageCache;
import com.liqihao.commons.enums.TaskStateCode;
import com.liqihao.pojo.baseMessage.TaskMessage;
import com.liqihao.pojo.bean.roleBean.MmoSimpleRole;
import com.liqihao.pojo.bean.taskBean.BaseTaskAction;
import com.liqihao.pojo.bean.taskBean.BaseTaskBean;
import com.liqihao.provider.TaskServiceProvider;
import com.liqihao.util.ScheduledThreadPoolUtil;

/**
 * @Classname OneBestEquipmentTaskBean
 * @Description 用一个极品装备
 * @Author lqhao
 * @Date 2021/1/27 15:27
 * @Version 1.0
 */
public class OneBestEquipmentTaskBean extends BaseTaskBean {
    @Override
    public void update(BaseTaskAction dto, MmoSimpleRole role) {
        OneBestEquipmentAction oneBestEquipmentAction= (OneBestEquipmentAction) dto;
        Integer level=oneBestEquipmentAction.getEquipmentLevel();
        TaskMessage taskMessage= TaskMessageCache.getInstance().get(getTaskMessageId());
        if (!getStatus().equals(TaskStateCode.FINISH.getCode())){
            //第一次
            if (level>getProgress()) {
                setProgress(level);
            }
            BaseTaskBean taskBean=this;
            checkFinish(taskMessage,taskBean,role);
        }
    }
}

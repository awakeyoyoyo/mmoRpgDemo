package com.liqihao.pojo.bean.taskBean.copySceneSuccessTask;

import com.liqihao.Cache.TaskMessageCache;
import com.liqihao.pojo.baseMessage.TaskMessage;
import com.liqihao.pojo.bean.taskBean.BaseTaskAction;
import com.liqihao.pojo.bean.taskBean.BaseTaskBean;
import com.liqihao.pojo.bean.roleBean.MmoSimpleRole;
import com.liqihao.provider.TaskServiceProvider;
import com.liqihao.util.ScheduledThreadPoolUtil;

/**
 * @Classname CopySceneTaskBean
 * @Description 副本通关任务
 * @Author lqhao
 * @Date 2021/1/26 10:55
 * @Version 1.0
 */
public class CopySceneTaskBean extends BaseTaskBean {

    @Override
    public void update(BaseTaskAction dto, MmoSimpleRole role) {
        CopySceneTaskAction copySceneTaskAction= (CopySceneTaskAction) dto;
        Integer copySceneId=copySceneTaskAction.getCopySceneId();
        TaskMessage taskMessage= TaskMessageCache.getInstance().get(getTaskMessageId());
        if (copySceneId.equals(taskMessage.getTargetId())){
            //是对应的副本
            setProgress(getProgress()+1);
            BaseTaskBean taskBean=this;
            checkFinish(taskMessage,taskBean,role);
        }

    }
}

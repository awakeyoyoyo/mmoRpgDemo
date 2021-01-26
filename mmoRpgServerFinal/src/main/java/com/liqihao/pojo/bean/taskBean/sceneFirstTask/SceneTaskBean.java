package com.liqihao.pojo.bean.taskBean.sceneFirstTask;

import com.liqihao.Cache.TaskMessageCache;
import com.liqihao.commons.enums.TaskStateCode;
import com.liqihao.pojo.baseMessage.TaskMessage;
import com.liqihao.pojo.bean.taskBean.BaseTaskAction;
import com.liqihao.pojo.bean.taskBean.BaseTaskBean;
import com.liqihao.pojo.bean.roleBean.MmoSimpleRole;
import com.liqihao.provider.TaskServiceProvider;
import com.liqihao.util.ScheduledThreadPoolUtil;

/**
 * @Classname SceneTaskBean
 * @Description 第一次进入场景
 * @Author lqhao
 * @Date 2021/1/26 11:06
 * @Version 1.0
 */
public class SceneTaskBean extends BaseTaskBean {
    @Override
    public void update(BaseTaskAction dto, MmoSimpleRole role) {
        SceneTaskAction sceneTaskAction= (SceneTaskAction) dto;
        Integer sceneId=sceneTaskAction.getSceneId();
        TaskMessage taskMessage= TaskMessageCache.getInstance().get(getTaskMessageId());
        if (!getStatus().equals(TaskStateCode.FINISH.getCode())&&sceneId.equals(taskMessage.getTargetId())){
            //第一次
            setProgress(getProgress()+1);
            Integer roleId=role.getId();
            BaseTaskBean taskBean=this;
            ScheduledThreadPoolUtil.addTask(() -> TaskServiceProvider.updateTaskDb(taskBean,roleId));
            checkFinish(taskMessage,role);
        }
    }
}

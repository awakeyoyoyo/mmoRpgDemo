package com.liqihao.pojo.bean.taskBean.roleLevelTask;

import com.liqihao.Cache.TaskMessageCache;
import com.liqihao.commons.enums.TaskStateCode;
import com.liqihao.pojo.baseMessage.TaskMessage;
import com.liqihao.pojo.bean.roleBean.MmoSimpleRole;
import com.liqihao.pojo.bean.taskBean.BaseTaskAction;
import com.liqihao.pojo.bean.taskBean.BaseTaskBean;
import com.liqihao.pojo.bean.taskBean.sceneFirstTask.SceneTaskAction;
import com.liqihao.provider.TaskServiceProvider;
import com.liqihao.util.ScheduledThreadPoolUtil;

/**
 * @Classname RoleLevleTaskBean
 * @Description 角色等级任务
 * @Author lqhao
 * @Date 2021/1/27 15:35
 * @Version 1.0
 */
public class RoleLevelTaskBean extends BaseTaskBean {
    @Override
    public void update(BaseTaskAction dto, MmoSimpleRole role) {
        RoleLevelAction roleLevelAction= (RoleLevelAction) dto;
        Integer level=roleLevelAction.getLevel();
        TaskMessage taskMessage= TaskMessageCache.getInstance().get(getTaskMessageId());
        if (!getStatus().equals(TaskStateCode.FINISH.getCode())){
            //第一次
            setProgress(level);
            BaseTaskBean taskBean=this;
            checkFinish(taskMessage,taskBean,role);
        }
    }
}

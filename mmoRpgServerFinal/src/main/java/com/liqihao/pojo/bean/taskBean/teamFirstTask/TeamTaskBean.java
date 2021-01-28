package com.liqihao.pojo.bean.taskBean.teamFirstTask;

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
 * @Classname TeamTaskBean
 * @Description 第一次加入队伍任务
 * @Author lqhao
 * @Date 2021/1/26 11:28
 * @Version 1.0
 */
public class TeamTaskBean extends BaseTaskBean {
    @Override
    public void update(BaseTaskAction dto, MmoSimpleRole role) {
        TaskMessage taskMessage= TaskMessageCache.getInstance().get(getTaskMessageId());
        if (!getStatus().equals(TaskStateCode.FINISH.getCode())){
            //第一次
            setProgress(getProgress()+1);
            BaseTaskBean taskBean=this;
            Integer roleId=role.getId();
            TaskServiceProvider.updateTaskDb(taskBean,roleId);
            checkFinish(taskMessage,role);
        }
    }
}

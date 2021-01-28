package com.liqihao.pojo.bean.taskBean.guildFirstTask;

import com.liqihao.Cache.TaskMessageCache;
import com.liqihao.commons.enums.TaskStateCode;
import com.liqihao.pojo.baseMessage.TaskMessage;
import com.liqihao.pojo.bean.roleBean.MmoSimpleRole;
import com.liqihao.pojo.bean.taskBean.BaseTaskAction;
import com.liqihao.pojo.bean.taskBean.BaseTaskBean;
import com.liqihao.provider.TaskServiceProvider;
import com.liqihao.util.ScheduledThreadPoolUtil;

/**
 * @Classname GuildFirstTaskBean
 * @Description 第一次加入公会事件任务
 * @Author lqhao
 * @Date 2021/1/27 15:22
 * @Version 1.0
 */
public class GuildFirstTaskBean extends BaseTaskBean {

    @Override
    public void update(BaseTaskAction dto, MmoSimpleRole role) {
        TaskMessage taskMessage= TaskMessageCache.getInstance().get(getTaskMessageId());
        if (!getStatus().equals(TaskStateCode.FINISH.getCode())){
            //第一次
            setProgress(getProgress()+1);
            Integer roleId=role.getId();
            BaseTaskBean taskBean=this;
            TaskServiceProvider.updateTaskDb(taskBean,roleId);
            checkFinish(taskMessage,role);
        }
    }
}

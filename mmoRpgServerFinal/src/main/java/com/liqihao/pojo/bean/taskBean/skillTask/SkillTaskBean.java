package com.liqihao.pojo.bean.taskBean.skillTask;

import com.liqihao.Cache.TaskMessageCache;
import com.liqihao.pojo.baseMessage.TaskMessage;
import com.liqihao.pojo.bean.roleBean.MmoSimpleRole;
import com.liqihao.pojo.bean.taskBean.BaseTaskAction;
import com.liqihao.pojo.bean.taskBean.BaseTaskBean;
import com.liqihao.provider.TaskServiceProvider;
import com.liqihao.util.ScheduledThreadPoolUtil;

/**
 * @Classname SkillTaskBean
 * @Description 使用技能任务
 * @Author lqhao
 * @Date 2021/1/26 11:12
 * @Version 1.0
 */
public class SkillTaskBean extends BaseTaskBean {
    @Override
    public void update(BaseTaskAction dto, MmoSimpleRole role) {
        SkillTaskAction skillTaskAction= (SkillTaskAction) dto;
        Integer skillId=skillTaskAction.getSkillId();
        TaskMessage taskMessage= TaskMessageCache.getInstance().get(getTaskMessageId());
        if (taskMessage.getTargetId()==null){
            //没有指定要使用什么技能
            setProgress(getProgress()+1);
            Integer roleId=role.getId();
            BaseTaskBean taskBean=this;
            TaskServiceProvider.updateTaskDb(taskBean,roleId);
            checkFinish(taskMessage,role);
        }else if (skillId.equals(taskMessage.getTargetId())){
            //使用特定技能
            setProgress(getProgress()+1);
            Integer roleId=role.getId();
            BaseTaskBean taskBean=this;
            TaskServiceProvider.updateTaskDb(taskBean,roleId);
            checkFinish(taskMessage,role);
        }

    }
}

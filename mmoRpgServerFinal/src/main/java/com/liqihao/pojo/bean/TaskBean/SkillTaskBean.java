package com.liqihao.pojo.bean.TaskBean;

import com.liqihao.Cache.TaskMessageCache;
import com.liqihao.commons.RpgServerException;
import com.liqihao.commons.enums.TaskStateCode;
import com.liqihao.commons.enums.TaskTargetTypeCode;
import com.liqihao.commons.enums.TaskTypeCode;
import com.liqihao.pojo.baseMessage.TaskMessage;
import com.liqihao.pojo.bean.roleBean.MmoSimpleRole;
import com.liqihao.provider.TaskServiceProvider;

/**
 * 使用技能任务
 * @author lqhao
 */
public class SkillTaskBean extends BaseTaskBean{
    @Override
    public void update(ActionDto dto, MmoSimpleRole role){
        if (!dto.getTargetType().equals(TaskTargetTypeCode.SKILL.getCode())){
            //不是该任务类型
            return;
        }
        if(getStatus().equals(TaskStateCode.FINISH.getCode())){
            return;
        }
        TaskMessage taskMessage= TaskMessageCache.getInstance().get(getTaskMessageId());
        //传入任务id
        if (taskMessage.getTargetId()==null) {
                //代表着使用了该类型物品即可 没指定具体
                setProgress(getProgress() + dto.getProgress());
                if (getProgress() >= taskMessage.getTargetProgress()) {
                    if (taskMessage.getType().equals(TaskTypeCode.TASK.getCode())) {
                        try {
                            TaskServiceProvider.abandonTask(taskMessage.getId(), role);
                        } catch (RpgServerException e) {
                        }
                    }else{
                        setStatus(TaskStateCode.FINISH.getCode());

                    }
                    sendFinishTask(role);
                }
            }else if (taskMessage.getTargetId().equals(dto.getTargetId())){
                //使用了指定的物品 增加进度
                setProgress(getProgress() + dto.getProgress());
                if (getProgress() >= taskMessage.getTargetProgress()) {
                    if (taskMessage.getType().equals(TaskTypeCode.TASK.getCode())) {
                        try {
                            TaskServiceProvider.abandonTask(taskMessage.getId(), role);
                        } catch (RpgServerException e) {
                        }
                    }else{
                        setStatus(TaskStateCode.FINISH.getCode());

                    }
                    sendFinishTask(role);
            }

        }
    }
}

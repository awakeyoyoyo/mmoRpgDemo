package com.liqihao.pojo.bean.taskBean.useTask;

import com.liqihao.cache.TaskMessageCache;
import com.liqihao.commons.enums.TaskStateCode;
import com.liqihao.commons.enums.TaskTargetTypeCode;
import com.liqihao.pojo.baseMessage.TaskMessage;
import com.liqihao.pojo.bean.taskBean.BaseTaskAction;
import com.liqihao.pojo.bean.taskBean.BaseTaskBean;
import com.liqihao.pojo.bean.roleBean.MmoSimpleRole;

/**
 * 使用某种物品任务实例
 * @author lqhao
 */
public class UseTaskBean extends BaseTaskBean {

    @Override
    public void update(BaseTaskAction taskAction, MmoSimpleRole role) {
        UseTaskAction useTaskAction= (UseTaskAction) taskAction;
        if (!useTaskAction.getTaskTargetType().equals(TaskTargetTypeCode.USE.getCode())){
            //不是该任务类型
            return;
        }
        if(getStatus().equals(TaskStateCode.FINISH.getCode())){
            return;
        }
        TaskMessage taskMessage=TaskMessageCache.getInstance().get(getTaskMessageId());
        if (!taskMessage.getArticleType().equals(useTaskAction.getArticleType())){
            //代表着该物品类型 不是该任务条件
            return;
        }else{
            if (taskMessage.getTargetId()==null) {
                //代表着使用了该类型物品即可 没指定具体
                setProgress(getProgress() + useTaskAction.getProgress());
            }else if (taskMessage.getTargetId().equals(useTaskAction.getTargetId())){
                //使用了指定的物品 增加进度
                setProgress(getProgress() + useTaskAction.getProgress());
            }
            BaseTaskBean taskBean=this;
            checkFinish(taskMessage,taskBean,role);
        }
    }


}

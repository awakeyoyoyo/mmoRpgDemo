package com.liqihao.pojo.bean.taskBean.talkTask;

import com.liqihao.cache.TaskMessageCache;
import com.liqihao.pojo.baseMessage.TaskMessage;
import com.liqihao.pojo.bean.roleBean.MmoSimpleRole;
import com.liqihao.pojo.bean.taskBean.BaseTaskAction;
import com.liqihao.pojo.bean.taskBean.BaseTaskBean;

/**
 * @Classname TalkTaskBean
 * @Description 对话任务
 * @Author lqhao
 * @Date 2021/1/26 11:18
 * @Version 1.0
 */
public class TalkTaskBean extends BaseTaskBean {
    @Override
    public void update(BaseTaskAction dto, MmoSimpleRole role) {
        TalkTaskAction taskAction= (TalkTaskAction) dto;
        Integer roleId=taskAction.getRoleId();
        TaskMessage taskMessage= TaskMessageCache.getInstance().get(getTaskMessageId());
        if (roleId.equals(taskMessage.getTargetId())){
            //相同
            setProgress(getProgress()+1);
            BaseTaskBean taskBean=this;
            checkFinish(taskMessage,taskBean,role);
        }

    }
}

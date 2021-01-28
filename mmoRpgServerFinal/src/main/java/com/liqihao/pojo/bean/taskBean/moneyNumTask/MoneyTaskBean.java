package com.liqihao.pojo.bean.taskBean.moneyNumTask;

import com.liqihao.Cache.TaskMessageCache;
import com.liqihao.pojo.baseMessage.TaskMessage;
import com.liqihao.pojo.bean.roleBean.MmoSimpleRole;
import com.liqihao.pojo.bean.taskBean.BaseTaskAction;
import com.liqihao.pojo.bean.taskBean.BaseTaskBean;
import com.liqihao.pojo.bean.taskBean.talkTask.TalkTaskAction;
import com.liqihao.provider.TaskServiceProvider;
import com.liqihao.util.ScheduledThreadPoolUtil;

/**
 * @Classname MoneyTaskBean
 * @Description 积累金币任务
 * @Author lqhao
 * @Date 2021/1/26 11:37
 * @Version 1.0
 */
public class MoneyTaskBean extends BaseTaskBean {
    @Override
    public void update(BaseTaskAction dto, MmoSimpleRole role) {
        MoneyTaskAction moneyTaskAction= (MoneyTaskAction) dto;
        Integer moneyAddNum=moneyTaskAction.getMoneyAddNum();
        TaskMessage taskMessage= TaskMessageCache.getInstance().get(getTaskMessageId());
        setProgress(getProgress()+moneyAddNum);
        Integer roleId=role.getId();
        BaseTaskBean taskBean=this;
        TaskServiceProvider.updateTaskDb(taskBean,roleId);
        checkFinish(taskMessage,role);
    }
}

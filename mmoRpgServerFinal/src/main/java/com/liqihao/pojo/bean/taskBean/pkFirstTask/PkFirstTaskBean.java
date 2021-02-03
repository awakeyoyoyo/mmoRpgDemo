package com.liqihao.pojo.bean.taskBean.pkFirstTask;

import com.liqihao.cache.TaskMessageCache;
import com.liqihao.commons.enums.TaskStateCode;
import com.liqihao.pojo.baseMessage.TaskMessage;
import com.liqihao.pojo.bean.roleBean.MmoSimpleRole;
import com.liqihao.pojo.bean.taskBean.BaseTaskAction;
import com.liqihao.pojo.bean.taskBean.BaseTaskBean;

/**
 * @Classname PkFirstTaskBean
 * @Description 第一次pk胜利任务
 * @Author lqhao
 * @Date 2021/1/27 15:25
 * @Version 1.0
 */
public class PkFirstTaskBean extends BaseTaskBean {
    @Override
    public void update(BaseTaskAction dto, MmoSimpleRole role) {
        TaskMessage taskMessage= TaskMessageCache.getInstance().get(getTaskMessageId());
        if (!getStatus().equals(TaskStateCode.FINISH.getCode())){
            //第一次
            setProgress(getProgress()+1);
            BaseTaskBean taskBean=this;
            checkFinish(taskMessage,taskBean,role);
        }
    }
}

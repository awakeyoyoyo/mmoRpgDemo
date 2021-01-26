package com.liqihao.pojo.bean.taskBean.killTask;

import com.liqihao.Cache.TaskMessageCache;
import com.liqihao.pojo.baseMessage.TaskMessage;
import com.liqihao.pojo.bean.taskBean.BaseTaskAction;
import com.liqihao.pojo.bean.taskBean.BaseTaskBean;
import com.liqihao.pojo.bean.roleBean.MmoSimpleRole;
import com.liqihao.provider.TaskServiceProvider;
import com.liqihao.util.ScheduledThreadPoolUtil;

/**
 * @Classname KillTaskBean
 * @Description 杀怪任务
 * @Author lqhao
 * @Date 2021/1/26 10:42
 * @Version 1.0
 */
public class KillTaskBean extends BaseTaskBean {
    @Override
    public void update(BaseTaskAction dto, MmoSimpleRole role) {
        KillTaskAction killTaskAction= (KillTaskAction) dto;
        Integer targetId=killTaskAction.getTargetRoleId();
        Integer roleType=killTaskAction.getRoleType();
        Integer number=killTaskAction.getNum();
        TaskMessage taskMessage= TaskMessageCache.getInstance().get(getTaskMessageId());
        if (roleType.equals(taskMessage.getArticleType())){
            //击杀的种类相同
            if (targetId.equals(taskMessage.getTargetId())){
                //击杀的角色id相同
                setProgress(getProgress()+number);
                Integer roleId=role.getId();
                BaseTaskBean taskBean=this;
                ScheduledThreadPoolUtil.addTask(() -> TaskServiceProvider.updateTaskDb(taskBean,roleId));
                checkFinish(taskMessage,role);
            }
        }
    }
}

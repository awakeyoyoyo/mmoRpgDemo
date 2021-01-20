package com.liqihao.pojo.bean.TaskBean;

import com.liqihao.Cache.TaskMessageCache;
import com.liqihao.commons.RpgServerException;
import com.liqihao.commons.enums.TaskStateCode;
import com.liqihao.commons.enums.TaskTargetTypeCode;
import com.liqihao.commons.enums.TaskTypeCode;
import com.liqihao.pojo.baseMessage.TaskMessage;
import com.liqihao.pojo.bean.roleBean.MmoSimpleRole;
import com.liqihao.provider.TaskServiceProvider;
import com.liqihao.util.ScheduledThreadPoolUtil;

/**
 * 进入某场景任务
 * @author lqhao
 */
public class SceneTaskBean extends BaseTaskBean{
    @Override
    public void update(ActionDto dto, MmoSimpleRole role) {
        if (!dto.getTargetType().equals(TaskTargetTypeCode.SCENE.getCode())){
            //不是该任务类型
            return;
        }
        if(getStatus().equals(TaskStateCode.FINISH.getCode())){
            return;
        }
        TaskMessage taskMessage= TaskMessageCache.getInstance().get(getTaskMessageId());
        if (taskMessage.getTargetId().equals(dto.getTargetId())){
                //进入了指定的:场景
            setProgress(getProgress() + dto.getProgress());
        }
        checkFinish(taskMessage,role);
    }
}

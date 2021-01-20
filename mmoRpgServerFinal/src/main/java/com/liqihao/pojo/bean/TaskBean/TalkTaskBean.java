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
 * 聊天任务
 * @author lqhao
 */
public class TalkTaskBean extends BaseTaskBean{
    @Override
    public void update(ActionDto dto, MmoSimpleRole role) {
        if (!dto.getTargetType().equals(TaskTargetTypeCode.TALK.getCode())){
            //不是该任务类型
            return;
        }
        if(getStatus().equals(TaskStateCode.FINISH.getCode())){
            return;
        }
        TaskMessage taskMessage= TaskMessageCache.getInstance().get(getTaskMessageId());
        //传入任务id
         if (taskMessage.getTargetId().equals(dto.getTargetId())){
            //使用了指定的物品 增加进度
            setProgress(getProgress() + dto.getProgress());
        }
        checkFinish(taskMessage,role);
    }
}

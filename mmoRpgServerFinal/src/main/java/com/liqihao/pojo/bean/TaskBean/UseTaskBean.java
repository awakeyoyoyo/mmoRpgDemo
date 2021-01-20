package com.liqihao.pojo.bean.TaskBean;

import com.liqihao.Cache.MedicineMessageCache;
import com.liqihao.Cache.TaskMessageCache;
import com.liqihao.commons.RpgServerException;
import com.liqihao.commons.enums.ArticleTypeCode;
import com.liqihao.commons.enums.TaskTargetTypeCode;
import com.liqihao.pojo.baseMessage.MedicineMessage;
import com.liqihao.pojo.baseMessage.TaskMessage;
import com.liqihao.pojo.bean.roleBean.MmoSimpleRole;
import com.liqihao.provider.TaskServiceProvider;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 任务实例
 * @author lqhao
 */
public class UseTaskBean extends BaseTaskBean {

    @Override
    public void update(ActionDto dto,MmoSimpleRole role) throws RpgServerException {
        if (!dto.getTargetType().equals(TaskTargetTypeCode.USE.getCode())){
            //不是该任务类型
            return;
        }
        TaskMessage taskMessage=TaskMessageCache.getInstance().get(getTaskMessageId());
        if (!taskMessage.getArticleType().equals(dto.getArticleType())){
            //代表着该物品类型 不在该任务条件中
            return;
        }else{
            if (taskMessage.getTargetId()==null) {
                //代表着使用了该物品即可 没指定具体
                setProgress(getProgress() + dto.getProgress());
                if (getProgress() >= taskMessage.getTargetProgress()) {
                    TaskServiceProvider.abandonTask(taskMessage.getId(), role);
                    //todo 发送消息完成了
                }
            }else if (taskMessage.getTargetId().equals(dto.getTargetId())){
                //使用了指定的物品 增加进度
                setProgress(getProgress() + dto.getProgress());
                if (getProgress() >= taskMessage.getTargetProgress()) {
                    TaskServiceProvider.abandonTask(taskMessage.getId(), role);
                    //todo 发送消息完成了
                }
            }
        }
    }


}

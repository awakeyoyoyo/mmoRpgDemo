package com.liqihao.pojo.bean.taskBean;

import com.liqihao.Cache.TaskMessageCache;
import com.liqihao.commons.ConstantValue;
import com.liqihao.commons.NettyResponse;
import com.liqihao.commons.StateCode;
import com.liqihao.commons.enums.ArticleTypeCode;
import com.liqihao.commons.enums.TaskStateCode;
import com.liqihao.commons.enums.TaskTypeCode;
import com.liqihao.pojo.baseMessage.MedicineMessage;
import com.liqihao.pojo.baseMessage.TaskMessage;
import com.liqihao.pojo.bean.articleBean.Article;
import com.liqihao.pojo.bean.articleBean.MedicineBean;
import com.liqihao.pojo.bean.roleBean.MmoSimpleRole;
import com.liqihao.protobufObject.TaskModel;
import com.liqihao.provider.ArticleServiceProvider;
import com.liqihao.provider.TaskServiceProvider;
import com.liqihao.util.DbUtil;
import com.liqihao.util.ScheduledThreadPoolUtil;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import io.netty.channel.Channel;

import javax.print.DocFlavor;


/**
 * @Classname BaseTaskAction
 * @Description 基础的任务执行动作消息
 * @Author lqhao
 * @Date 2021/1/21 12:19
 * @Version 1.0
 */
public abstract class BaseTaskBean {
    /**
     * 任务信息类
     */
    private Integer taskMessageId;
    /**
     * 进展
     */
    private Integer progress;
    /**
     * 状态
     */
    private Integer status;
     /**
     * 数据库id
     */
    private Integer taskDbId;
    /**
     * 接收事件
     */
    private long createTime;
    /**
     * 任务目标类型
     */
    private Integer taskTargetTypeId;

    /**
     * 检测是否完成
     * @param dto
     * @param role
     */
    public abstract void update(BaseTaskAction dto,MmoSimpleRole role);

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public Integer getTaskMessageId() {
        return taskMessageId;
    }

    public void setTaskMessageId(Integer taskMessageId) {
        this.taskMessageId = taskMessageId;
    }

    public Integer getProgress() {
        return progress;
    }

    public void setProgress(Integer progress) {
        this.progress = progress;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getTaskDbId() {
        return taskDbId;
    }

    public void setTaskDbId(Integer taskDbId) {
        this.taskDbId = taskDbId;
    }

    public Integer getTaskTargetTypeId() {
        return taskTargetTypeId;
    }

    public void setTaskTargetTypeId(Integer taskTargetTypeId) {
        this.taskTargetTypeId = taskTargetTypeId;
    }

    /**
     * description 发送任务完成消息
     * @param role
     * @return {@link null }
     * @author lqhao
     * @createTime 2021/1/26 15:49
     */
    public void sendFinishTask(MmoSimpleRole role){
        Channel channel=role.getChannel();
        NettyResponse nettyResponse = new NettyResponse();
        nettyResponse.setCmd(ConstantValue.FINISH_TASK_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        //protobuf
        TaskModel.TaskModelMessage.Builder messageBuilder = TaskModel.TaskModelMessage.newBuilder();
        messageBuilder.setDataType(TaskModel.TaskModelMessage.DateType.FinishTaskResponse);
        messageBuilder.setFinishTaskResponse(TaskModel.FinishTaskResponse.newBuilder().setTaskMessageId(getTaskMessageId()).build());
        nettyResponse.setData(messageBuilder.build().toByteArray());
        channel.writeAndFlush(nettyResponse);
    }
    /**
     * description 检车是否满足任务条件
     * @param taskMessage
     * @param role
     * @return {@link null }
     * @author lqhao
     * @createTime 2021/1/26 15:49
     */
    public void checkFinish(TaskMessage taskMessage,MmoSimpleRole role){
        if (getProgress() >= taskMessage.getTargetProgress()) {
            if (taskMessage.getType().equals(TaskTypeCode.TASK.getCode())) {
                role.getTaskManager().getTaskBeans().remove(taskMessage.getId());
                Integer taskBeanId=getTaskDbId();
                ScheduledThreadPoolUtil.addTask(() -> TaskServiceProvider.deleteTaskDb(taskBeanId));
            }else{
                setStatus(TaskStateCode.FINISH.getCode());
            }
            reward(taskMessage,role);
            sendFinishTask(role);
        }
        if (taskMessage.getNextTaskId()!=-1){
            //还有后续任务
            TaskMessage nextTaskMessage= TaskMessageCache.getInstance().get(taskMessage.getNextTaskId());
            TaskServiceProvider.acceptTask(nextTaskMessage,role);
        }
    }

    /**
     * description 奖励
     * @return {@link null }
     * @author lqhao
     * @createTime 2021/1/26 10:51
     */
    public void reward(TaskMessage taskMessage,MmoSimpleRole role){
        Integer articleMessageId=taskMessage.getRewardArticleMessageId();
        Integer rewardNum=taskMessage.getRewardNum();
        Integer rewardArticleType=taskMessage.getRewardArticleType();
        //金币类型
        if (rewardArticleType.equals(ArticleTypeCode.MONEY.getCode())){
            role.moneyLock.writeLock().lock();
            try{
                role.setMoney(role.getMoney()+rewardNum);
                DbUtil.updateRole(role);
            }finally {
                role.moneyLock.writeLock().unlock();
            }
            return;
        }
        //装备、药品
        Article article=null;
        if (rewardArticleType.equals(ArticleTypeCode.EQUIPMENT.getCode())) {
            article=ArticleServiceProvider.productEquipment(articleMessageId);
        }else if (rewardArticleType.equals(ArticleTypeCode.MEDICINE.getCode())){
            MedicineBean medicineBean=ArticleServiceProvider.productMedicine(articleMessageId);
            medicineBean.setQuantity(rewardNum);
            article=medicineBean;
        }
        //放入背包
        role.getBackpackManager().put(article,role.getId());
    }
}

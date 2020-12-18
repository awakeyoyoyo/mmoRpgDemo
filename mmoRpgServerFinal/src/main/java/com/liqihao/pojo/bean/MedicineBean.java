package com.liqihao.pojo.bean;

import com.liqihao.Cache.ChannelMessageCache;
import com.liqihao.Cache.OnlineRoleMessageCache;
import com.liqihao.Cache.SceneBeanMessageCache;
import com.liqihao.commons.ConstantValue;
import com.liqihao.commons.NettyResponse;
import com.liqihao.commons.enums.*;
import com.liqihao.pojo.baseMessage.MedicineMessage;
import com.liqihao.protobufObject.PlayModel;
import com.liqihao.util.ScheduledThreadPoolUtil;
import io.netty.channel.Channel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class MedicineBean extends MedicineMessage implements Article{

    private Integer quantity;
    private Integer articleId;
    private Integer bagId;//数据库行记录id

    public Integer getBagId() {
        return bagId;
    }

    public void setBagId(Integer bagId) {
        this.bagId = bagId;
    }


    public boolean useMedicene(Integer roleId){
        //判断是瞬间恢复还是持续性恢复
        if (getMedicineType().equals(MedicineTypeCode.MOMENT.getCode())){
            MmoSimpleRole mmoSimpleRole= OnlineRoleMessageCache.getInstance().get(roleId);
            Integer addNumber=getDamageValue();
            if (getDamageType().equals(DamageTypeCode.MP.getCode())) {
                Integer oldMp = mmoSimpleRole.getNowMp();
                Integer newNumber = oldMp + addNumber;
                if (newNumber > mmoSimpleRole.getMp()) {
                    mmoSimpleRole.setNowMp(mmoSimpleRole.getMp());
                    addNumber = mmoSimpleRole.getMp() - oldMp;
                } else {
                    mmoSimpleRole.setNowMp(newNumber);
                }
            }else{
                Integer oldHP = mmoSimpleRole.getNowBlood();
                Integer newNumber = oldHP + addNumber;
                if (newNumber > mmoSimpleRole.getBlood()) {
                    mmoSimpleRole.setNowBlood(mmoSimpleRole.getBlood());
                    addNumber = mmoSimpleRole.getBlood() - oldHP;
                    //发送数据包
                } else {
                    mmoSimpleRole.setNowBlood(newNumber);
                }
            }
            PlayModel.RoleIdDamage.Builder damageU = PlayModel.RoleIdDamage.newBuilder();
            damageU.setFromRoleId(mmoSimpleRole.getId());
            damageU.setToRoleId(mmoSimpleRole.getId());
            damageU.setAttackStyle(AttackStyleCode.MEDICENE.getCode());
            damageU.setBufferId(-1);
            damageU.setDamage(addNumber);
            damageU.setDamageType(getDamageType());
            damageU.setMp(mmoSimpleRole.getNowMp());
            damageU.setNowblood(mmoSimpleRole.getNowBlood());
            damageU.setSkillId(-1);
            damageU.setState(mmoSimpleRole.getStatus());
            PlayModel.PlayModelMessage.Builder myMessageBuilder = PlayModel.PlayModelMessage.newBuilder();
            myMessageBuilder.setDataType(PlayModel.PlayModelMessage.DateType.DamagesNoticeResponse);
            PlayModel.DamagesNoticeResponse.Builder damagesNoticeBuilder = PlayModel.DamagesNoticeResponse.newBuilder();
            damagesNoticeBuilder.setRoleIdDamage(damageU);
            myMessageBuilder.setDamagesNoticeResponse(damagesNoticeBuilder.build());
            NettyResponse nettyResponse = new NettyResponse();
            nettyResponse.setCmd(ConstantValue.DAMAGES_NOTICE_RESPONSE);
            nettyResponse.setStateCode(StateCode.SUCCESS);
            nettyResponse.setData(myMessageBuilder.build().toByteArray());

            Integer sceneId = mmoSimpleRole.getMmosceneid();
            List<Integer> players = SceneBeanMessageCache.getInstance().get(sceneId).getRoles();
            for (Integer playerId : players) {
                Channel cc = ChannelMessageCache.getInstance().get(playerId);
                if (cc != null) {
                    cc.writeAndFlush(nettyResponse);
                }
            }
            return  true;
        }else {
            //判断是否已经有持续性药品任务
            ConcurrentHashMap<String, ScheduledFuture<?>> replyMpRoleMap = ScheduledThreadPoolUtil.getReplyMpRole();
            String key = roleId.toString();
            if (getDamageType().equals(DamageTypeCode.MP.getCode())) {
                key = key + "MP";
            } else {
                key = key + "HP";
            }
            if (!replyMpRoleMap.containsKey(key)) {
                //传入每秒恢复量
                Integer lastTime=getLastTime();
                Integer secondValue=getSecondValue();
                ScheduledThreadPoolUtil.ReplyMpTask replyMpTask = new ScheduledThreadPoolUtil.ReplyMpTask(roleId, secondValue, getDamageType(), key,lastTime);
                // 周期性执行，每3秒执行一次
                ScheduledFuture<?> t = ScheduledThreadPoolUtil.getScheduledExecutorService().scheduleAtFixedRate(replyMpTask, 0, 1, TimeUnit.SECONDS);
                replyMpRoleMap.put(key, t);
                return true;
            } else {
                //已经有持续性恢复药品在使用 无法再使用
                return false;
            }
        }
    }


    public Integer getArticleId() {
        return articleId;
    }

    public void setArticleId(Integer articleId) {
        this.articleId = articleId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @Override
    public Integer getArticleTypeCode() {
        return getArticleType();
    }
}

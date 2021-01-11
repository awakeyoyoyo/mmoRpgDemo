package com.liqihao.pojo.bean.bufferBean;

import com.liqihao.Cache.BufferMessageCache;
import com.liqihao.Cache.ChannelMessageCache;
import com.liqihao.Cache.SceneBeanMessageCache;
import com.liqihao.commons.ConstantValue;
import com.liqihao.commons.NettyResponse;
import com.liqihao.commons.StateCode;
import com.liqihao.commons.enums.AttackStyleCode;
import com.liqihao.commons.enums.ConsumeTypeCode;
import com.liqihao.commons.enums.DamageTypeCode;
import com.liqihao.commons.enums.RoleTypeCode;
import com.liqihao.pojo.baseMessage.BufferMessage;
import com.liqihao.pojo.bean.roleBean.Role;
import com.liqihao.protobufObject.PlayModel;
import com.liqihao.provider.CopySceneProvider;
import io.netty.channel.Channel;

import java.util.List;

/**
 * buffer Bean
 * @author lqhao
 */
public class BaseBufferBean {
    private Integer fromRoleId;
    private Integer toRoleId;
    private Long createTime;
    private Integer fromRoleType;
    private Integer toRoleType;
    private Integer BufferMessageId;

    public Integer getBufferMessageId() {
        return BufferMessageId;
    }

    public void setBufferMessageId(Integer bufferMessageId) {
        BufferMessageId = bufferMessageId;
    }

    public Integer getFromRoleType() {
        return fromRoleType;
    }

    public void setFromRoleType(Integer fromRoleType) {
        this.fromRoleType = fromRoleType;
    }

    public Integer getToRoleType() {
        return toRoleType;
    }

    public void setToRoleType(Integer toRoleType) {
        this.toRoleType = toRoleType;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Integer getFromRoleId() {
        return fromRoleId;
    }

    public void setFromRoleId(Integer fromRoleId) {
        this.fromRoleId = fromRoleId;
    }

    public Integer getToRoleId() {
        return toRoleId;
    }

    public void setToRoleId(Integer toRoleId) {
        this.toRoleId = toRoleId;
    }

    public void effectToPeople(Role toRole){
    }
    public void effectToRole(Role toRole){
    }

    public void sendAllRoleDamage(Role toRole){
        BufferMessage bufferMessage=BufferMessageCache.getInstance().get(getBufferMessageId());
        //生成数据包
        PlayModel.PlayModelMessage.Builder myMessageBuilder = PlayModel.PlayModelMessage.newBuilder();
        myMessageBuilder.setDataType(PlayModel.PlayModelMessage.DateType.DamagesNoticeResponse);
        PlayModel.DamagesNoticeResponse.Builder damagesNoticeBuilder = PlayModel.DamagesNoticeResponse.newBuilder();
        PlayModel.RoleIdDamage.Builder damageU = PlayModel.RoleIdDamage.newBuilder();
        damageU.setDamageType(DamageTypeCode.HP.getCode()).setAttackStyle(AttackStyleCode.BUFFER.getCode())
                .setDamage(bufferMessage.getBuffNum()).setFromRoleId(getFromRoleId()).setToRoleId(getToRoleId())
                .setState(toRole.getStatus()).setMp(toRole.getNowMp()).setBufferId(getBufferMessageId()).setNowblood(toRole.getNowHp());
        damagesNoticeBuilder.setRoleIdDamage(damageU);
        myMessageBuilder.setDamagesNoticeResponse(damagesNoticeBuilder.build());
        NettyResponse nettyResponse = new NettyResponse();
        nettyResponse.setCmd(ConstantValue.DAMAGES_NOTICE_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        nettyResponse.setData(myMessageBuilder.build().toByteArray());
        //广播信息
        List<Integer> players;
        if (toRole.getMmoSceneId()!=null) {
            players = SceneBeanMessageCache.getInstance().get(toRole.getMmoSceneId()).getRoles();
            for (Integer playerId:players){
                Channel c= ChannelMessageCache.getInstance().get(playerId);
                if (c!=null){
                    c.writeAndFlush(nettyResponse);
                }
            }

        }else{
            List<Role> roles = CopySceneProvider.getCopySceneBeanById(toRole.getCopySceneBeanId()).getRoles();
            for (Role role:roles) {
                if (role.getType().equals(RoleTypeCode.PLAYER.getCode())){
                    Channel c= ChannelMessageCache.getInstance().get(role.getId());
                    if (c!=null){
                        c.writeAndFlush(nettyResponse);
                    }
                }
            }
        }
    }

    public  PlayModel.RoleIdDamage.Builder builderRoleDamage(Role toRole){
        //扣血类型
        PlayModel.RoleIdDamage.Builder damageU = PlayModel.RoleIdDamage.newBuilder();
        damageU.setFromRoleId(getFromRoleId());
        damageU.setFromRoleType(getFromRoleType());
        damageU.setToRoleId(toRole.getId());
        damageU.setToRoleType(toRole.getType());
        damageU.setBufferId(getBufferMessageId());
        damageU.setSkillId(-1);
        return damageU;
    }
}

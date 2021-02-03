package com.liqihao.pojo.bean.buffBean;

import com.googlecode.protobuf.format.JsonFormat;
import com.liqihao.commons.ConstantValue;
import com.liqihao.commons.NettyResponse;
import com.liqihao.commons.StateCode;
import com.liqihao.commons.enums.AttackStyleCode;
import com.liqihao.pojo.bean.roleBean.Role;
import com.liqihao.protobufObject.PlayModel;
import com.liqihao.util.NotificationUtil;

/**
 * 嘲讽类型buffer
 * @author lqhao
 */
public class AttractBuffBean extends BaseBuffBean {

    @Override
    public void effectToPeople(Role toRole){
        //嘲讽类型
        PlayModel.RoleIdDamage.Builder damageU =builderRoleDamage(toRole);
        damageU.setAttackStyle(AttackStyleCode.GG_ATTACK.getCode());
        PlayModel.PlayModelMessage.Builder myMessageBuilder = PlayModel.PlayModelMessage.newBuilder();
        myMessageBuilder.setDataType(PlayModel.PlayModelMessage.DateType.DamagesNoticeResponse);
        PlayModel.DamagesNoticeResponse.Builder damagesNoticeBuilder = PlayModel.DamagesNoticeResponse.newBuilder();
        damagesNoticeBuilder.setRoleIdDamage(damageU);
        myMessageBuilder.setDamagesNoticeResponse(damagesNoticeBuilder.build());
        NettyResponse nettyResponse = new NettyResponse();
        nettyResponse.setCmd(ConstantValue.DAMAGES_NOTICE_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        nettyResponse.setData(myMessageBuilder.build().toByteArray());
        //广播信息
        String json= JsonFormat.printToString(myMessageBuilder.build());
        NotificationUtil.notificationSceneRole(nettyResponse,toRole,json);
    }
    @Override
    public void effectToRole(Role toRole,Role fromRole){
        PlayModel.RoleIdDamage.Builder damageU =builderRoleDamage(toRole);
        damageU.setAttackStyle(AttackStyleCode.GG_ATTACK.getCode());
        PlayModel.PlayModelMessage.Builder myMessageBuilder = PlayModel.PlayModelMessage.newBuilder();
        myMessageBuilder.setDataType(PlayModel.PlayModelMessage.DateType.DamagesNoticeResponse);
        PlayModel.DamagesNoticeResponse.Builder damagesNoticeBuilder = PlayModel.DamagesNoticeResponse.newBuilder();
        damagesNoticeBuilder.setRoleIdDamage(damageU);
        myMessageBuilder.setDamagesNoticeResponse(damagesNoticeBuilder.build());
        NettyResponse nettyResponse = new NettyResponse();
        nettyResponse.setCmd(ConstantValue.DAMAGES_NOTICE_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        nettyResponse.setData(myMessageBuilder.build().toByteArray());
        //广播信息
        String json= JsonFormat.printToString(myMessageBuilder.build());
        NotificationUtil.notificationSceneRole(nettyResponse,toRole,json);
    }
}

package com.liqihao.pojo.bean;

import com.liqihao.Cache.ChannelMessageCache;
import com.liqihao.commons.ConstantValue;
import com.liqihao.commons.NettyResponse;
import com.liqihao.commons.StateCode;
import com.liqihao.pojo.baseMessage.CopySceneMessage;
import com.liqihao.protobufObject.SceneModel;
import com.liqihao.provider.CopySceneProvider;
import io.netty.channel.Channel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * 副本bean
 * @author lqhao
 */
public class CopySceneBean extends CopySceneMessage {
    private long createTime;
    private long endTime;
    private List<BossBean> bossBeans;
    private List<MmoSimpleRole> mmoSimpleRoles;
    private Integer status;
    private Integer copySceneBeanId;
    public Integer getCopySceneBeanId() {
        return copySceneBeanId;
    }

    public void setCopySceneBeanId(Integer copySceneBeanId) {
        this.copySceneBeanId = copySceneBeanId;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public List<BossBean> getBossBeans() {
        return bossBeans;
    }

    public void setBossBeans(List<BossBean> bossBeans) {
        this.bossBeans = bossBeans;
    }

    public List<MmoSimpleRole> getMmoSimpleRoles() {
        return mmoSimpleRoles;
    }

    public void setMmoSimpleRoles(List<MmoSimpleRole> mmoSimpleRoles) {
        this.mmoSimpleRoles = mmoSimpleRoles;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * 玩家退出副本
     * @param id
     */
    public void peopleExit(Integer id) {
        Iterator iterator=mmoSimpleRoles.iterator();
        while (iterator.hasNext()){
            MmoSimpleRole mmoSimpleRole= (MmoSimpleRole) iterator.next();
            if (mmoSimpleRole.getId().equals(id)){
                iterator.remove();
                Channel c= ChannelMessageCache.getInstance().get(id);
                mmoSimpleRole.setCopySceneId(null);
                //todo 退出副本
//                c.writeAndFlush()
                break;
            }
        }
    }
    //副本结束
    public void end() {
        Iterator iterator=mmoSimpleRoles.iterator();
        while (iterator.hasNext()){
            MmoSimpleRole mmoSimpleRole= (MmoSimpleRole) iterator.next();
            iterator.remove();
            Channel c= ChannelMessageCache.getInstance().get(mmoSimpleRole.getId());
            mmoSimpleRole.setCopySceneId(null);
            //todo 退出副本
//            c.writeAndFlush()
            //让用户回到原来的场景
            Integer nextSceneId=mmoSimpleRole.getLastSceneId();
            mmoSimpleRole.setLastSceneId(null);
            List<MmoSimpleRole> nextRoles=mmoSimpleRole.wentScene(nextSceneId);
            NettyResponse nettyResponse=new NettyResponse();
            nettyResponse.setCmd(ConstantValue.WENT_RESPONSE);
            nettyResponse.setStateCode(StateCode.SUCCESS);
            SceneModel.SceneModelMessage.Builder builder=SceneModel.SceneModelMessage.newBuilder();
            builder.setDataType(SceneModel.SceneModelMessage.DateType.WentResponse);
            SceneModel.WentResponse.Builder wentResponsebuilder=SceneModel.WentResponse.newBuilder();
            //simpleRole
            List<SceneModel.RoleDTO> roleDTOS=new ArrayList<>();
            for (MmoSimpleRole mmoRole :nextRoles){
                SceneModel.RoleDTO.Builder msr=SceneModel.RoleDTO.newBuilder();
                msr.setId(mmoRole.getId());
                msr.setName(mmoRole.getName());
                msr.setType(mmoRole.getType());
                msr.setStatus(mmoRole.getStatus());
                msr.setOnStatus(mmoRole.getOnstatus());
                msr.setBlood(mmoRole.getBlood());
                msr.setNowBlood(mmoRole.getNowBlood());
                msr.setMp(mmoRole.getMp());
                msr.setNowMp(mmoRole.getNowMp());
                msr.setAttack(mmoRole.getAttack());
                msr.setAttackAdd(mmoRole.getDamageAdd());
                SceneModel.RoleDTO msrobject=msr.build();
                roleDTOS.add(msrobject);
            }
            wentResponsebuilder.setSceneId(nextSceneId);
            wentResponsebuilder.addAllRoleDTO(roleDTOS);
            builder.setWentResponse(wentResponsebuilder.build());
            byte[] data2=builder.build().toByteArray();
            nettyResponse.setData(data2);
            c.writeAndFlush(nettyResponse);
        }
        //copySceneProvider删除该副本
        CopySceneProvider.deleteNewCopySceneById(getCopySceneBeanId());
    }
}

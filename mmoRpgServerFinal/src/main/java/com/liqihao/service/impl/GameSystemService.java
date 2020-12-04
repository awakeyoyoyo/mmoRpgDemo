package com.liqihao.service.impl;

import com.liqihao.Cache.MmoCahe;
import com.liqihao.commons.*;
import com.liqihao.dao.MmoRolePOJOMapper;
import com.liqihao.pojo.MmoRolePOJO;
import com.liqihao.pojo.MmoScene;
import com.liqihao.pojo.MmoSimpleRole;
import com.liqihao.protobufObject.GameSystemModel;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GameSystemService implements com.liqihao.service.GameSystemService {
    @Autowired
    private MmoRolePOJOMapper mmoRolePOJOMapper;
    @Override
    public NettyResponse netIoOutTime(NettyRequest nettyRequest, Channel channel) {
        //获取相同的channel对应的roleId 然后根据其删除缓存中的信息
        ConcurrentHashMap<Integer,Channel> channelConcurrentHashMap=MmoCahe.getInstance().getChannelConcurrentHashMap();
        Integer roleId=null;
        synchronized (channelConcurrentHashMap) {
            for (Integer key : channelConcurrentHashMap.keySet()) {
                   if (channelConcurrentHashMap.get(key).equals(channel)) {
                       roleId = key;
                   }
               }
               if (roleId != null) {
                channelConcurrentHashMap.remove(roleId);
               }
         }
        //删除缓存中的信息
        if (roleId!=null) {
            ConcurrentHashMap<Integer, MmoRolePOJO> mmsHashMap = MmoCahe.getInstance().getMmoSimpleRoleConcurrentHashMap();
            MmoRolePOJO rolePOJO;
            rolePOJO=mmsHashMap.get(roleId);
            rolePOJO.setOnstatus(RoleOnStatusCode.EXIT.getCode());
            mmsHashMap.put(rolePOJO.getId(),rolePOJO);
            ConcurrentHashMap<Integer, MmoScene> mmoSceneHashMap = MmoCahe.getInstance().getMmoSceneConcurrentHashMap();
            synchronized (mmoSceneHashMap) {
                MmoScene mmoScene = mmoSceneHashMap.get(rolePOJO.getMmosceneid());
                List<MmoSimpleRole> mmoSimpleRoles = mmoScene.getRoles();
                Iterator<MmoSimpleRole> iterator = mmoSimpleRoles.iterator();
                while (iterator.hasNext()) {
                    MmoSimpleRole sbi = iterator.next();
                    if (sbi.getId() == rolePOJO.getId()) {
                        iterator.remove();
                        break;
                    }
                }
            }
            //修改数据库
            mmoRolePOJOMapper.updateByPrimaryKeySelective(rolePOJO);
        }

        //用户下线
        NettyResponse response=new NettyResponse();
        response.setCmd(ConstantValue.OUT_RIME_RESPONSE);
        response.setModule(ConstantValue.GAME_SYSTEM_MODULE);
        response.setStateCode(StateCode.SUCCESS);
        GameSystemModel.GameSystemModelMessage modelMessage=GameSystemModel.GameSystemModelMessage.newBuilder()
                .setDataType(GameSystemModel.GameSystemModelMessage.DateType.OutTimeResponse)
                .setOutTimeResponse(GameSystemModel.OutTimeResponse.newBuilder().setMessage("太久没活动了，服务器已断开连接").build()).build();
        response.setData(modelMessage.toByteArray());
        return response;
    }
}
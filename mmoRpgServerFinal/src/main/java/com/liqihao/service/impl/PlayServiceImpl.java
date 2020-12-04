package com.liqihao.service.impl;

import com.google.protobuf.InvalidProtocolBufferException;
import com.liqihao.Cache.MmoCache;
import com.liqihao.commons.*;
import com.liqihao.dao.MmoRolePOJOMapper;
import com.liqihao.dao.MmoScenePOJOMapper;
import com.liqihao.dao.MmoUserPOJOMapper;
import com.liqihao.pojo.*;
import com.liqihao.protobufObject.PlayModel;
import com.liqihao.service.PlayService;
import com.liqihao.util.CommonsUtil;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PlayServiceImpl implements PlayService{
    @Autowired
    private MmoRolePOJOMapper mmoRolePOJOMapper;
    @Autowired
    private MmoUserPOJOMapper mmoUserPOJOMapper;
    @Autowired
    private MmoScenePOJOMapper mmoScenePOJOMapper;

    @Override
    public NettyResponse registerRequest(NettyRequest nettyRequest,Channel channel) throws InvalidProtocolBufferException {
        byte[] data=nettyRequest.getData();
        PlayModel.PlayModelMessage myMessage;
        myMessage=PlayModel.PlayModelMessage.parseFrom(data);
        String username=myMessage.getRegisterRequest().getUsername();
        String password=myMessage.getRegisterRequest().getPassword();
        String roleName=myMessage.getRegisterRequest().getRolename();
        Integer count1=mmoUserPOJOMapper.selectByUsername(username);
        Integer count2=mmoRolePOJOMapper.selectByRoleName(roleName);
        if (count1>0||count2>0){
            //用户已存在
            NettyResponse nettyResponse=new NettyResponse();
            nettyResponse.setCmd(ConstantValue.REGISTER_RESPONSE);
            nettyResponse.setStateCode(StateCode.SUCCESS);
            nettyResponse.setModule(ConstantValue.PLAY_MODULE);
            //protobuf 生成registerResponse
            PlayModel.PlayModelMessage.Builder messageData=PlayModel.PlayModelMessage.newBuilder();
            messageData.setDataType(PlayModel.PlayModelMessage.DateType.LoginResponse);
            PlayModel.RegisterResponse.Builder registerResponseBuilder=PlayModel.RegisterResponse.newBuilder();
            registerResponseBuilder.setMessage("用户已存在or角色名已经存在");
            registerResponseBuilder.setStateCode(StateCode.FAIL);
            messageData.setRegisterResponse(registerResponseBuilder.build());
            nettyResponse.setData(messageData.build().toByteArray());
            return nettyResponse;
        }
        //注册成功 数据库插入账号信息
        MmoRolePOJO mmoRolePOJO=new MmoRolePOJO();
        mmoRolePOJO.setName(roleName);
        mmoRolePOJO.setMmosceneid(1);
        mmoRolePOJO.setStatus(RoleStatusCode.ALIVE.getCode());
        mmoRolePOJO.setOnstatus(RoleOnStatusCode.EXIT.getCode());
        mmoRolePOJO.setType(RoleTypeCode.PLAYER.getCode());
        mmoRolePOJOMapper.insert(mmoRolePOJO);
        MmoUserPOJO mmoUserPOJO=new MmoUserPOJO();
        mmoUserPOJO.setUserroleid(mmoRolePOJO.getId().toString());
        mmoUserPOJO.setUsername(username);
        mmoUserPOJO.setUserpwd(password);
        mmoUserPOJOMapper.insert(mmoUserPOJO);

        //返回成功的数据包
        NettyResponse nettyResponse=new NettyResponse();
        nettyResponse.setCmd(ConstantValue.REGISTER_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        nettyResponse.setModule(ConstantValue.PLAY_MODULE);
        //protobuf 生成registerResponse
        PlayModel.PlayModelMessage.Builder messageData=PlayModel.PlayModelMessage.newBuilder();
        messageData.setDataType(PlayModel.PlayModelMessage.DateType.RegisterResponse);
        PlayModel.RegisterResponse.Builder registerResponseBuilder=PlayModel.RegisterResponse.newBuilder();
        registerResponseBuilder.setMessage("用户注册成功");
        registerResponseBuilder.setStateCode(200);
        messageData.setRegisterResponse(registerResponseBuilder.build());
        nettyResponse.setData(messageData.build().toByteArray());
        return nettyResponse;
    }

    @Override
    public NettyResponse loginRequest(NettyRequest nettyRequest,Channel channel) throws InvalidProtocolBufferException {
        byte[] data=nettyRequest.getData();
        PlayModel.PlayModelMessage myMessage;
        myMessage=PlayModel.PlayModelMessage.parseFrom(data);
        String username=myMessage.getLoginRequest().getUsername();
        String password=myMessage.getLoginRequest().getPassword();
        Integer mmoUserId=mmoUserPOJOMapper.checkByUernameAndPassword(username,password);
        if (null==mmoUserId||mmoUserId<0){
            NettyResponse nettyResponse=new NettyResponse();
            nettyResponse.setCmd(ConstantValue.LOGIN_RESPONSE);
            nettyResponse.setStateCode(StateCode.FAIL);
            nettyResponse.setModule(ConstantValue.PLAY_MODULE);
            nettyResponse.setData("密码错误or账号错误".getBytes());
            return nettyResponse;
        }
        //将角色设置为在线模式
        MmoUserPOJO mmoUserPOJO=mmoUserPOJOMapper.selectByPrimaryKey(mmoUserId);
//        MmoRolePOJO mmoRolePOJO=mmoRolePOJOMapper.selectByPrimaryKey(Integer.parseInt(mmoUserPOJO.getUserroleid()));
//        mmoRolePOJO.setOnstatus(RoleOnStatusCode.ONLINE.getCode());
        //从缓存中获取,且修改其为在线模式
        ConcurrentHashMap<Integer,MmoRolePOJO> rolesMap= MmoCache.getInstance().getMmoSimpleRoleConcurrentHashMap();
        MmoRolePOJO role=rolesMap.get(Integer.parseInt(mmoUserPOJO.getUserroleid()));
        role.setOnstatus(RoleOnStatusCode.ONLINE.getCode());
        //设置缓存中的场景中新增该角色
        ConcurrentHashMap<Integer,MmoScene> sceneMap= MmoCache.getInstance().getMmoSceneConcurrentHashMap();
        MmoScene mmoScene=sceneMap.get(role.getMmosceneid());
        List<MmoSimpleRole> mmoSimpleRoles=mmoScene.getRoles();
        MmoSimpleRole temp=new MmoSimpleRole();
        temp.setId(role.getId());
        temp.setOnstatus(RoleOnStatusCode.getValue(role.getOnstatus()));
        temp.setStatus(RoleStatusCode.getValue(role.getStatus()));
        temp.setType(RoleTypeCode.getValue(role.getType()));
        temp.setName(role.getName());
        mmoSimpleRoles.add(temp);
        //放入线程池中异步处理
        //数据库中人物状态         //todo 提交给线程池异步执行
        mmoRolePOJOMapper.updateByPrimaryKeySelective(role);
        //将channel绑定用户信息存储
        ConcurrentHashMap<Integer, Channel> channelConcurrentHashMap= MmoCache.getInstance().getChannelConcurrentHashMap();
        channelConcurrentHashMap.put(role.getId(),channel);

        //获取 场景信息和自身角色信息
        List<MmoSimpleScene> canScenes=mmoScene.getCanScene();
        List<MmoSimpleRole> roundRoles=mmoScene.getRoles();
        //protobuf 生成loginResponse
        PlayModel.PlayModelMessage.Builder messageData=PlayModel.PlayModelMessage.newBuilder();
        messageData.setDataType(PlayModel.PlayModelMessage.DateType.LoginResponse);
        PlayModel.LoginResponse.Builder loginResponseBuilder=PlayModel.LoginResponse.newBuilder();
        PlayModel.MmoSimpleRole.Builder mmoSimpleRoleBuilder=PlayModel.MmoSimpleRole.newBuilder();
        //自身角色信息
        PlayModel.MmoSimpleRole mmoSimpleRole=mmoSimpleRoleBuilder.setId(role.getId())
                .setName(role.getName())
                .setOnStatus(RoleOnStatusCode.getValue(role.getOnstatus()))
                .setStatus(RoleStatusCode.getValue(role.getStatus()))
                .setType(RoleTypeCode.getValue(role.getType())).build();

        loginResponseBuilder.setMmoSimpleRole(mmoSimpleRole);
        //场景信息
        PlayModel.MmoScene.Builder mmoSceneBuilder=PlayModel.MmoScene.newBuilder();
        mmoSceneBuilder.setId(mmoScene.getId()).setPlaceName(mmoScene.getPlaceName());
        //protobuf simpleCanScene
        List<PlayModel.MmoSimpleScene> simScenes=new ArrayList<>();
        for (MmoSimpleScene mms:canScenes){
            PlayModel.MmoSimpleScene.Builder mss=PlayModel.MmoSimpleScene.newBuilder();
            mss.setId(mms.getId());
            mss.setPlaceName(mms.getPalceName());
            PlayModel.MmoSimpleScene mssobject=mss.build();
            simScenes.add(mssobject);
        }
        mmoSceneBuilder.addAllCanScene(simScenes);
        //protobuf simpleRoles
        List<PlayModel.MmoSimpleRole> simRoles=new ArrayList<>();
        for (MmoSimpleRole mmoRole :roundRoles){
            PlayModel.MmoSimpleRole.Builder msr=PlayModel.MmoSimpleRole.newBuilder();
            msr.setId(mmoRole.getId());
            msr.setName(mmoRole.getName());
            msr.setType(mmoRole.getType());
            msr.setStatus(mmoRole.getStatus());
            msr.setOnStatus(mmoRole.getOnstatus());
            PlayModel.MmoSimpleRole msrobject=msr.build();
            simRoles.add(msrobject);
        }
        mmoSceneBuilder.addAllCanScene(simScenes);
        mmoSceneBuilder.addAllRoles(simRoles);
        loginResponseBuilder.setMmoScene(mmoSceneBuilder.build());
        //打包成messageData
        messageData.setLoginResponse(loginResponseBuilder.build());
        NettyResponse nettyResponse=new NettyResponse();
        nettyResponse.setData(messageData.build().toByteArray());
        nettyResponse.setModule(ConstantValue.PLAY_MODULE);
        nettyResponse.setCmd(ConstantValue.LOGIN_RESPONSE);
        return nettyResponse;
    }

    @Override
    public NettyResponse logoutRequest(NettyRequest nettyRequest,Channel channel) throws InvalidProtocolBufferException {
        byte[] data=nettyRequest.getData();
        PlayModel.PlayModelMessage myMessage;
        myMessage=PlayModel.PlayModelMessage.parseFrom(data);
        Integer rolesId=myMessage.getLogoutRequest().getRolesId();
        //将数据库中设置为离线
        //todo 提交给线程池异步执行
        MmoRolePOJO mmoRolePOJO=mmoRolePOJOMapper.selectByPrimaryKey(rolesId);
        mmoRolePOJO.setOnstatus(RoleOnStatusCode.EXIT.getCode());
        mmoRolePOJOMapper.updateByPrimaryKeySelective(mmoRolePOJO);
        //todo 从缓存或者是spring容器中的场景信息获取 需解决并发安全问题
        MmoScenePOJO mmoScenePOJO=mmoScenePOJOMapper.selectByPrimaryKey(mmoRolePOJO.getMmosceneid());
        List<Integer> sceneRoles=CommonsUtil.split(mmoScenePOJO.getRoles());
        if (sceneRoles.contains(rolesId)){
            sceneRoles.remove(rolesId);
        }
        //缓存中的场景角色 设置为离线  角色表中的在线改为离线
        ConcurrentHashMap<Integer,MmoScene> sceneMap= MmoCache.getInstance().getMmoSceneConcurrentHashMap();
        ConcurrentHashMap<Integer,MmoRolePOJO> rolesMap= MmoCache.getInstance().getMmoSimpleRoleConcurrentHashMap();
        MmoRolePOJO role;
        role = rolesMap.get(rolesId);
        role.setOnstatus(RoleOnStatusCode.EXIT.getCode());
        rolesMap.put(role.getId(), role);
        synchronized (sceneMap) {
            MmoScene mmoScene = sceneMap.get(role.getMmosceneid());
            List<MmoSimpleRole> mmoSimpleRoles = mmoScene.getRoles();
            Iterator<MmoSimpleRole> iterator = mmoSimpleRoles.iterator();
            while (iterator.hasNext()) {
                MmoSimpleRole sbi = iterator.next();
                if (sbi.getId() == role.getId()) {
                    iterator.remove();
                    break;
                }
            }
        }
        //删除缓存中 channel绑定的信息
        ConcurrentHashMap<Integer,Channel> channelConcurrentHashMap= MmoCache.getInstance().getChannelConcurrentHashMap();
        channelConcurrentHashMap.remove(role.getId());

        //protobuf生成消息
        PlayModel.PlayModelMessage.Builder myMessageBuilder=PlayModel.PlayModelMessage.newBuilder();
        myMessageBuilder.setDataType(PlayModel.PlayModelMessage.DateType.LogoutResponse);
        PlayModel.LogoutResponse.Builder logoutResponseBuilder=PlayModel.LogoutResponse.newBuilder();
        logoutResponseBuilder.setCode(StateCode.SUCCESS);
        logoutResponseBuilder.setMxg("退出登陆成功");
        myMessageBuilder.setLogoutResponse(logoutResponseBuilder.build());
        //封装成nettyResponse
        NettyResponse nettyResponse=new NettyResponse();
        nettyResponse.setCmd(ConstantValue.LOGOUT_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        nettyResponse.setModule(ConstantValue.PLAY_MODULE);
        nettyResponse.setData(myMessageBuilder.build().toByteArray());
        return  nettyResponse;
    }


}

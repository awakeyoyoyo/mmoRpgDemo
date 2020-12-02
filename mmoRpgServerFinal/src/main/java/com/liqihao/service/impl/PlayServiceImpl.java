package com.liqihao.service.impl;

import com.google.protobuf.InvalidProtocolBufferException;
import com.liqihao.commons.*;
import com.liqihao.dao.MmoRolePOJOMapper;
import com.liqihao.dao.MmoScenePOJOMapper;
import com.liqihao.dao.MmoUserPOJOMapper;
import com.liqihao.pojo.MmoRolePOJO;
import com.liqihao.pojo.MmoScenePOJO;
import com.liqihao.pojo.MmoUserPOJO;
import com.liqihao.protobufObject.PlayModel;
import com.liqihao.service.PlayService;
import com.liqihao.util.CommonsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PlayServiceImpl implements PlayService {
    @Autowired
    private MmoRolePOJOMapper mmoRolePOJOMapper;
    @Autowired
    private MmoUserPOJOMapper mmoUserPOJOMapper;
    @Autowired
    private MmoScenePOJOMapper mmoScenePOJOMapper;
    @Override
    public NettyResponse registerRequest(NettyRequest nettyRequest) throws InvalidProtocolBufferException {
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

        MmoRolePOJO mmoRolePOJO=new MmoRolePOJO();
        mmoRolePOJO.setName(roleName);
        mmoRolePOJO.setMmosceneid(1);
        mmoRolePOJO.setStatus(RoleStatusCode.ALIVE.getCode());
        mmoRolePOJO.setOnstatus(RoleOnStatusCode.EXIT.getCode());
        mmoRolePOJO.setType(RoleTypeCode.PLAYER.getCode());
        Integer id=mmoRolePOJOMapper.insert(mmoRolePOJO);
        MmoUserPOJO mmoUserPOJO=new MmoUserPOJO();
        mmoUserPOJO.setUserroleid(id.toString());
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
    public NettyResponse loginRequest(NettyRequest nettyRequest) throws InvalidProtocolBufferException {
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
            //protobuf 生成loginResponse
            PlayModel.PlayModelMessage.Builder messageData=PlayModel.PlayModelMessage.newBuilder();
            messageData.setDataType(PlayModel.PlayModelMessage.DateType.LoginResponse);
            nettyResponse.setData(messageData.build().toByteArray());
            return nettyResponse;
        }
        //将角色设置为在线模式
        MmoUserPOJO mmoUserPOJO=mmoUserPOJOMapper.selectByPrimaryKey(mmoUserId);
        MmoRolePOJO mmoRolePOJO=mmoRolePOJOMapper.selectByPrimaryKey(Integer.parseInt(mmoUserPOJO.getUserroleid()));
        mmoRolePOJO.setOnstatus(RoleOnStatusCode.ONLINE.getCode());
        mmoRolePOJOMapper.updateByPrimaryKeySelective(mmoRolePOJO);
        //获取 场景信息和自身角色信息
        MmoScenePOJO mmoScenePOJO=mmoScenePOJOMapper.selectByPrimaryKey(mmoRolePOJO.getMmosceneid());
        List<MmoScenePOJO> canScenes=new ArrayList<>();
        List<MmoRolePOJO> roundRoles=new ArrayList<>();
        List<Integer> cansceneIds= CommonsUtil.split(mmoScenePOJO.getCanscene());
        if (cansceneIds.size()>0) {
            for (Integer id : cansceneIds) {
                canScenes.add(mmoScenePOJOMapper.selectByPrimaryKey(id));
            }
        }
        List<Integer> roundRoleIds= CommonsUtil.split(mmoScenePOJO.getRoles());
        if (roundRoleIds.size()>0) {
            for (Integer id : roundRoleIds) {
                roundRoles.add(mmoRolePOJOMapper.selectByPrimaryKey(id));
            }
        }
        //protobuf 生成loginResponse
        PlayModel.PlayModelMessage.Builder messageData=PlayModel.PlayModelMessage.newBuilder();
        messageData.setDataType(PlayModel.PlayModelMessage.DateType.LoginResponse);
        PlayModel.LoginResponse.Builder loginResponseBuilder=PlayModel.LoginResponse.newBuilder();
        PlayModel.MmoSimpleRole.Builder mmoSimpleRoleBuilder=PlayModel.MmoSimpleRole.newBuilder();
        //自身角色信息
        PlayModel.MmoSimpleRole mmoSimpleRole=mmoSimpleRoleBuilder.setId(mmoRolePOJO.getId())
                .setName(mmoRolePOJO.getName())
                .setOnStatus(RoleOnStatusCode.getValue(mmoRolePOJO.getOnstatus()))
                .setStatus(RoleStatusCode.getValue(mmoRolePOJO.getStatus()))
                .setType(RoleTypeCode.getValue(mmoRolePOJO.getType())).build();

        loginResponseBuilder.setMmoSimpleRole(mmoSimpleRole);
        //场景信息
        PlayModel.MmoScene.Builder mmoSceneBuilder=PlayModel.MmoScene.newBuilder();
        mmoSceneBuilder.setId(mmoScenePOJO.getId()).setPlaceName(mmoScenePOJO.getPlacename());
        //protobuf simpleCanScene
        List<PlayModel.MmoSimpleScene> simScenes=new ArrayList<>();
        for (MmoScenePOJO mmoScene:canScenes){
            PlayModel.MmoSimpleScene.Builder mss=PlayModel.MmoSimpleScene.newBuilder();
            mss.setId(mmoScene.getId());
            mss.setPlaceName(mmoScene.getPlacename());
            PlayModel.MmoSimpleScene mssobject=mss.build();
            simScenes.add(mssobject);
        }
        mmoSceneBuilder.addAllCanScene(simScenes);
        //protobuf simpleRoles
        List<PlayModel.MmoSimpleRole> simRoles=new ArrayList<>();
        for (MmoRolePOJO mmoRole :roundRoles){
            PlayModel.MmoSimpleRole.Builder msr=PlayModel.MmoSimpleRole.newBuilder();
            msr.setId(mmoRole.getId());
            msr.setName(mmoRole.getName());
            msr.setType(RoleTypeCode.getValue(mmoRole.getType()));
            msr.setStatus(RoleStatusCode.getValue(mmoRole.getStatus()));
            msr.setOnStatus(RoleStatusCode.getValue(mmoRole.getOnstatus()));
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
    public NettyResponse logoutRequest(NettyRequest nettyRequest) throws InvalidProtocolBufferException {
        byte[] data=nettyRequest.getData();
        PlayModel.PlayModelMessage myMessage;
        myMessage=PlayModel.PlayModelMessage.parseFrom(data);
        Integer rolesId=myMessage.getLogoutRequest().getRolesId();
        //将数据库中设置为离线
        MmoRolePOJO mmoRolePOJO=mmoRolePOJOMapper.selectByPrimaryKey(rolesId);
        mmoRolePOJO.setOnstatus(RoleOnStatusCode.EXIT.getCode());
        mmoRolePOJOMapper.updateByPrimaryKeySelective(mmoRolePOJO);
        //将场景中玩家id 移除
        MmoScenePOJO mmoScenePOJO=mmoScenePOJOMapper.selectByPrimaryKey(mmoRolePOJO.getMmosceneid());
        List<Integer> sceneRoles=CommonsUtil.split(mmoScenePOJO.getRoles());
        if (sceneRoles.contains(rolesId)){
            sceneRoles.remove(rolesId);
        }
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

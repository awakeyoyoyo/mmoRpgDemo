package com.liqihao.service.impl;

import com.google.protobuf.InvalidProtocolBufferException;
import com.liqihao.Cache.MmoCache;
import com.liqihao.annotation.HandlerCmdTag;
import com.liqihao.annotation.HandlerServiceTag;
import com.liqihao.commons.*;
import com.liqihao.dao.MmoRolePOJOMapper;
import com.liqihao.dao.MmoScenePOJOMapper;
import com.liqihao.dao.MmoUserPOJOMapper;
import com.liqihao.pojo.*;
import com.liqihao.protobufObject.PlayModel;
import com.liqihao.service.PlayService;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;

@Service
@HandlerServiceTag
public class PlayServiceImpl implements PlayService{
    @Autowired
    private MmoRolePOJOMapper mmoRolePOJOMapper;
    @Autowired
    private MmoUserPOJOMapper mmoUserPOJOMapper;
    @Autowired
    private MmoScenePOJOMapper mmoScenePOJOMapper;

    @Override
    @HandlerCmdTag(cmd = ConstantValue.REGISTER_REQUEST,module = ConstantValue.PLAY_MODULE)
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
//            nettyResponse.setModule(ConstantValue.PLAY_MODULE);
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
    @HandlerCmdTag(cmd = ConstantValue.LOGIN_REQUEST,module = ConstantValue.PLAY_MODULE)
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
            nettyResponse.setData("密码错误or账号错误".getBytes());
            return nettyResponse;
        }
        //将角色设置为在线模式
        MmoUserPOJO mmoUserPOJO=mmoUserPOJOMapper.selectByPrimaryKey(mmoUserId);
        //从数据库中读取角色,且修改其为在线模式，放入角色在线集合
        MmoRolePOJO role=mmoRolePOJOMapper.selectByPrimaryKey(Integer.parseInt(mmoUserPOJO.getUserroleid()));
        role.setOnstatus(RoleOnStatusCode.ONLINE.getCode());
        //缓存
        ConcurrentHashMap<Integer,MmoRolePOJO> rolesMap= MmoCache.getInstance().getMmoSimpleRoleConcurrentHashMap();
        rolesMap.put(role.getId(),role);
        //放入线程池中异步处理
        //数据库中人物状态         //todo 提交给线程池异步执行
        mmoRolePOJOMapper.updateByPrimaryKeySelective(role);
        //将channel绑定用户信息存储
        ConcurrentHashMap<Integer, Channel> channelConcurrentHashMap= MmoCache.getInstance().getChannelConcurrentHashMap();
        channelConcurrentHashMap.put(role.getId(),channel);

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
        loginResponseBuilder.setSceneId(role.getMmosceneid());
        //打包成messageData
        messageData.setLoginResponse(loginResponseBuilder.build());
        NettyResponse nettyResponse=new NettyResponse();
        nettyResponse.setData(messageData.build().toByteArray());
        nettyResponse.setCmd(ConstantValue.LOGIN_RESPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        return nettyResponse;
    }

    @Override
    @HandlerCmdTag(cmd = ConstantValue.LOGOUT_REQUEST,module = ConstantValue.PLAY_MODULE)
    public NettyResponse logoutRequest(NettyRequest nettyRequest,Channel channel) throws InvalidProtocolBufferException {
        ConcurrentHashMap<Integer,Channel> channelConcurrentHashMap= MmoCache.getInstance().getChannelConcurrentHashMap();
        Integer roleId=null;
        synchronized (channelConcurrentHashMap) {
            for (Integer key : channelConcurrentHashMap.keySet()) {
                if (channelConcurrentHashMap.get(key).equals(channel)) {
                    roleId = key;
                }
            }
            if (roleId != null) {
                //删除缓存中 channel绑定的信息
                channelConcurrentHashMap.remove(roleId);
            }
        }
        if (roleId==null){
            NettyResponse errotResponse=new NettyResponse(StateCode.FAIL,ConstantValue.LOGOUT_RESPONSE,"请先登录".getBytes());
            return  errotResponse;
        }
        //将数据库中设置为离线
        //todo 提交给线程池异步执行

        MmoRolePOJO mmoRolePOJO=mmoRolePOJOMapper.selectByPrimaryKey(roleId);
        mmoRolePOJO.setOnstatus(RoleOnStatusCode.EXIT.getCode());
        mmoRolePOJOMapper.updateByPrimaryKeySelective(mmoRolePOJO);
        MmoScenePOJO mmoScenePOJO=mmoScenePOJOMapper.selectByPrimaryKey(mmoRolePOJO.getMmosceneid());
        //缓存角色集合删除
        ConcurrentHashMap<Integer,MmoRolePOJO> rolesMap= MmoCache.getInstance().getMmoSimpleRoleConcurrentHashMap();
        rolesMap.remove(roleId);

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
        nettyResponse.setData(myMessageBuilder.build().toByteArray());
        return  nettyResponse;
    }


}

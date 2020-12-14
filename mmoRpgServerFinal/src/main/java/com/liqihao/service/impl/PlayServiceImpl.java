package com.liqihao.service.impl;

import com.google.protobuf.InvalidProtocolBufferException;
import com.liqihao.Cache.MmoCache;
import com.liqihao.annotation.HandlerCmdTag;
import com.liqihao.annotation.HandlerServiceTag;
import com.liqihao.commons.*;
import com.liqihao.commons.enums.*;
import com.liqihao.dao.MmoRolePOJOMapper;
import com.liqihao.dao.MmoScenePOJOMapper;
import com.liqihao.dao.MmoUserPOJOMapper;
import com.liqihao.pojo.*;
import com.liqihao.pojo.baseMessage.BaseRoleMessage;
import com.liqihao.pojo.baseMessage.BufferMessage;
import com.liqihao.pojo.baseMessage.SkillMessage;
import com.liqihao.pojo.bean.*;
import com.liqihao.protobufObject.PlayModel;
import com.liqihao.service.PlayService;
import com.liqihao.util.CommonsUtil;
import com.liqihao.util.ScheduledThreadPoolUtil;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.*;

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
        ConcurrentHashMap<Integer, MmoSimpleRole> rolesMap= MmoCache.getInstance().getMmoSimpleRoleConcurrentHashMap();
        MmoSimpleRole simpleRole=new MmoSimpleRole();
        simpleRole.setId(role.getId());
        simpleRole.setMmosceneid(role.getMmosceneid());
        simpleRole.setName(role.getName());
        simpleRole.setOnstatus(role.getOnstatus());
        simpleRole.setStatus(role.getStatus());
        simpleRole.setType(role.getType());
        simpleRole.setSkillIds(role.getSkillIds());
        //从基础信息获取
        BaseRoleMessage baseRoleMessage=MmoCache.getInstance().getBaseRoleMessage();
        simpleRole.setBlood(baseRoleMessage.getHp());
        simpleRole.setNowBlood(baseRoleMessage.getHp());
        simpleRole.setMp(baseRoleMessage.getMp());
        simpleRole.setNowMp(baseRoleMessage.getMp());
        simpleRole.setAttack(baseRoleMessage.getAttack());
        List<Integer> skillIds=CommonsUtil.split(simpleRole.getSkillIds());
        simpleRole.setSkillIdList(skillIds);
        simpleRole.setCdMap(new HashMap<Integer, Long>());
        simpleRole.setBufferBeans(new CopyOnWriteArrayList<>());
        rolesMap.put(role.getId(),simpleRole);
        //放入线程池中异步处理
        //数据库中人物状态         //todo 提交给线程池异步执行
        mmoRolePOJOMapper.updateByPrimaryKeySelective(role);
        //将channel绑定用户信息存储
        ConcurrentHashMap<Integer, Channel> channelConcurrentHashMap= MmoCache.getInstance().getChannelConcurrentHashMap();
        MmoCache.getInstance().getIdChannelConcurrentHashMap().put(channel,role.getId());
        channelConcurrentHashMap.put(role.getId(),channel);

        //protobuf 生成loginResponse
        PlayModel.PlayModelMessage.Builder messageData=PlayModel.PlayModelMessage.newBuilder();
        messageData.setDataType(PlayModel.PlayModelMessage.DateType.LoginResponse);
        PlayModel.LoginResponse.Builder loginResponseBuilder=PlayModel.LoginResponse.newBuilder();
        PlayModel.RoleDTO.Builder mmoSimpleRoleBuilder=PlayModel.RoleDTO.newBuilder();
        //自身角色信息
        PlayModel.RoleDTO roleDTO=mmoSimpleRoleBuilder.setId(simpleRole.getId())
                .setName(simpleRole.getName())
                .setOnStatus(simpleRole.getOnstatus())
                .setStatus(simpleRole.getStatus())
                .setType(simpleRole.getType())
                .setBlood(simpleRole.getBlood())
                .setNowBlood(simpleRole.getBlood())
                .addAllSkillIdList(skillIds)
                .setMp(simpleRole.getMp())
                .setNowMp(simpleRole.getNowMp())
                .build();
        loginResponseBuilder.setRoleDto(roleDTO);
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
        ConcurrentHashMap<Integer,MmoSimpleRole> rolesMap= MmoCache.getInstance().getMmoSimpleRoleConcurrentHashMap();
        rolesMap.remove(roleId);
        MmoCache.getInstance().getIdChannelConcurrentHashMap().remove(channel);
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

    @Override
    @HandlerCmdTag(cmd = ConstantValue.USE_SKILL_REQUEST,module = ConstantValue.PLAY_MODULE)
    public NettyResponse useSkillRequest(NettyRequest nettyRequest, Channel channel) throws InvalidProtocolBufferException {
        byte[] data=nettyRequest.getData();
        PlayModel.PlayModelMessage myMessage;
        myMessage=PlayModel.PlayModelMessage.parseFrom(data);
        Integer skillId=myMessage.getUseSkillRequest().getSkillId();
        Integer sceneId=myMessage.getUseSkillRequest().getSceneId();
        Integer roleId=MmoCache.getInstance().getIdChannelConcurrentHashMap().get(channel);
        if (roleId==null){
            return new NettyResponse(StateCode.FAIL,ConstantValue.USE_SKILL_RSPONSE, "未登陆".getBytes());
        }
        //判断cd
        MmoSimpleRole mmoSimpleRole=MmoCache.getInstance().getMmoSimpleRoleConcurrentHashMap().get(roleId);
        Long nextTime= mmoSimpleRole.getCdMap().get(skillId);
        if (nextTime!=null){
            if (System.currentTimeMillis()<nextTime){
                return new NettyResponse(StateCode.FAIL,ConstantValue.USE_SKILL_RSPONSE, "该技能cd中。。".getBytes());
            }
        }
        //从缓存中查找出 怪物
        ConcurrentHashMap<Integer, MmoSimpleNPC> npcMap=MmoCache.getInstance().getNpcMessageConcurrentHashMap();
        ArrayList<MmoSimpleNPC> target=new ArrayList<>();
        ArrayList<Integer> players=new ArrayList<>();
        for (Integer npcId:npcMap.keySet()) {
            MmoSimpleNPC npc=npcMap.get(npcId);
            if (npc.getMmosceneid().equals(sceneId)&&npc.getType().equals(RoleTypeCode.ENEMY.getCode())){
                if (npc.getStatus().equals(RoleStatusCode.ALIVE.getCode())){
                    target.add(npc);
                }

            }
        }
        ConcurrentHashMap<Integer, MmoSimpleRole> roleMap=MmoCache.getInstance().getMmoSimpleRoleConcurrentHashMap();
        for (Integer npcId:roleMap.keySet()) {
            MmoSimpleRole role=roleMap.get(npcId);
            if (role.getMmosceneid().equals(sceneId)&&role.getType().equals(RoleTypeCode.PLAYER.getCode())&&!role.getId().equals(roleId)){
                players.add(role.getId());
            }
        }
        //从缓存中找出技能
        SkillMessage skillMessage=MmoCache.getInstance().getSkillMessageConcurrentHashMap().get(skillId);
        SkillBean skillBean=new SkillBean();
        skillBean.setId(skillMessage.getId());
        skillBean.setConsumeType(skillMessage.getConsumeType());
        skillBean.setConsumeNum(skillMessage.getConsumeNum());
        skillBean.setCd(skillMessage.getCd());
        skillBean.setBufferIds(CommonsUtil.split(skillMessage.getBufferIds()));
        skillBean.setBaseDamage(skillMessage.getBaseDamage());
        skillBean.setSkillName(skillMessage.getSkillName());
        skillBean.setAddPercon(skillMessage.getAddPercon());
        skillBean.setSkillType(skillMessage.getSkillType());
        //角色扣篮或者扣血
        if (skillBean.getConsumeType().equals(ConsuMeTypeCode.HP.getCode())){
            //扣血
            //判断血量是否足够
            if (mmoSimpleRole.getNowBlood()<skillBean.getConsumeNum()){
                //血量不够
                return new NettyResponse(StateCode.FAIL,ConstantValue.USE_SKILL_RSPONSE, "血量不够无法使用该技能".getBytes());
            }
            mmoSimpleRole.setNowBlood(mmoSimpleRole.getNowBlood()-skillBean.getConsumeNum());

        }else {
            //扣篮
            //判断蓝量是否足够
            if (mmoSimpleRole.getNowMp()<skillBean.getConsumeNum()){
                //蓝量不够
                return new NettyResponse(StateCode.FAIL,ConstantValue.USE_SKILL_RSPONSE, "蓝量不够无法使用该技能".getBytes());
            }
            mmoSimpleRole.setNowMp(mmoSimpleRole.getNowMp()-skillBean.getConsumeNum());
            //判断是否已经有自动回蓝任务
            ConcurrentHashMap<Integer, ScheduledFuture<?>> replyMpRoleMap=ScheduledThreadPoolUtil.getReplyMpRole();
            if (!replyMpRoleMap.containsKey(roleId)) {
                //number为空 代表着自动回蓝
                ScheduledThreadPoolUtil.ReplyMpTask replyMpTask = new ScheduledThreadPoolUtil.ReplyMpTask(roleId, null);
                // 周期性执行，每5秒执行一次
                ScheduledFuture<?> t=ScheduledThreadPoolUtil.getScheduledExecutorService().scheduleAtFixedRate(replyMpTask, 0, 5, TimeUnit.SECONDS);
                replyMpRoleMap.put(roleId,t);
            }
        }
        List<PlayModel.RoleIdDamage> list=new ArrayList<>();
        // 生成一个角色扣血或者扣篮
        PlayModel.RoleIdDamage.Builder damageU=PlayModel.RoleIdDamage.newBuilder();
        damageU.setFromRoleId(roleId);
        damageU.setToRoleId(roleId);
        damageU.setAttackStyle(AttackStyleCode.USESKILL.getCode());
        damageU.setBufferId(-1);
        damageU.setDamage(skillBean.getConsumeNum());
        damageU.setDamageType(skillBean.getConsumeType());
        damageU.setMp(mmoSimpleRole.getNowMp());
        damageU.setNowblood(mmoSimpleRole.getNowBlood());
        damageU.setSkillId(skillBean.getId());
        damageU.setState(mmoSimpleRole.getStatus());
        list.add(damageU.build());
        //攻击怪物
        for (MmoSimpleNPC mmoSimpleNPC:target){

            Integer hp=mmoSimpleNPC.getNowBlood();
            Integer reduce=0;
            if (skillBean.getSkillType().equals(SkillTypeCode.FIED.getCode())){
                //固伤 只有技能伤害
                hp-=skillBean.getBaseDamage();
                reduce=skillBean.getBaseDamage();
            }
            if(skillBean.getSkillType().equals(SkillTypeCode.PERCENTAGE.getCode())){
                //百分比 按照攻击力比例增加
                Integer damage=skillBean.getBaseDamage();
                damage=(int)Math.ceil(damage+mmoSimpleNPC.getAttack()*skillBean.getAddPercon());
                hp=hp-damage;
                reduce=damage;
            }
            if (hp<=0){
                reduce=reduce+hp;
                hp=0;
                mmoSimpleNPC.setStatus(RoleStatusCode.DIE.getCode());
            }
            mmoSimpleNPC.setNowBlood(hp);
            // 扣血伤害
            PlayModel.RoleIdDamage.Builder damageR=PlayModel.RoleIdDamage.newBuilder();
            damageR.setFromRoleId(roleId);
            damageR.setToRoleId(mmoSimpleNPC.getId());
            damageR.setAttackStyle(AttackStyleCode.ATTACK.getCode());
            damageR.setBufferId(-1);
            damageR.setDamage(reduce);
            damageR.setDamageType(DamageTypeCode.HP.getCode());
            damageR.setMp(mmoSimpleNPC.getNowMp());
            damageR.setNowblood(mmoSimpleNPC.getNowBlood());
            damageR.setSkillId(skillBean.getId());
            damageR.setState(mmoSimpleNPC.getStatus());
            list.add(damageR.build());
            //怪物攻击本人
            if (!mmoSimpleNPC.getStatus().equals(RoleStatusCode.DIE.getCode())){
                npcAttack(mmoSimpleNPC.getId(),roleId);
            }
        }

        //查看是否有buffer
        List<Integer> buffers=skillBean.getBufferIds();
        List<BufferBean> bufferBeans=new ArrayList<>();
        for (Integer buffId:buffers) {
            BufferMessage b=MmoCache.getInstance().getBufferMessageConcurrentHashMap().get(buffId);
            for (MmoSimpleNPC mmoSimpleNPC:target) {
                //生成buffer类
                BufferBean bufferBean=new BufferBean();
                bufferBean.setFromRoleId(roleId);
                bufferBean.setBuffNum(b.getBuffNum());
                bufferBean.setBuffType(b.getBuffType());
                bufferBean.setName(b.getName());
                bufferBean.setId(b.getId());
                bufferBean.setLastTime(b.getLastTime());
                bufferBean.setSpaceTime(b.getSpaceTime());
                bufferBean.setCreateTime(System.currentTimeMillis());
                bufferBeans.add(bufferBean);
                bufferBean.setToRoleId(mmoSimpleNPC.getId());
                Integer count=bufferBean.getLastTime()/bufferBean.getSpaceTime();
                //增加bufferid
                mmoSimpleNPC.getBufferBeans().add(bufferBean);
                //线程池中放入任务
                ScheduledThreadPoolUtil.BufferTask bufferTask = new ScheduledThreadPoolUtil.BufferTask(bufferBean, count);
                //查看是否已经有了该buffer 有则覆盖无则直接加入
                Integer key=Integer.parseInt(mmoSimpleNPC.getId().toString()+bufferBean.getId().toString());
                ConcurrentHashMap<Integer,ScheduledFuture<?>> bufferRole=ScheduledThreadPoolUtil.getBufferRole();
                if (bufferRole.containsKey(key)){
                    bufferRole.get(key).cancel(false);
                }
                ScheduledFuture<?> t =ScheduledThreadPoolUtil.getScheduledExecutorService().scheduleAtFixedRate(bufferTask,0,bufferBean.getSpaceTime(),TimeUnit.SECONDS);
                bufferRole.put(key,t);
            }
        }
        //cd
        Map<Integer,Long> map=mmoSimpleRole.getCdMap();
        Long time=System.currentTimeMillis();
        int addTime=skillBean.getCd()*1000;
        map.put(skillBean.getId(),time+addTime);


        //封装成nettyResponse
        PlayModel.PlayModelMessage.Builder myMessageBuilder=PlayModel.PlayModelMessage.newBuilder();
        myMessageBuilder.setDataType(PlayModel.PlayModelMessage.DateType.UseSkillResponse);
        PlayModel.UseSkillResponse.Builder useSkillBuilder=PlayModel.UseSkillResponse.newBuilder();
        useSkillBuilder.addAllRoleIdDamages(list);
        myMessageBuilder.setUseSkillResponse(useSkillBuilder.build());
        NettyResponse nettyResponse=new NettyResponse();
        nettyResponse.setCmd(ConstantValue.USE_SKILL_RSPONSE);
        nettyResponse.setStateCode(StateCode.SUCCESS);
        nettyResponse.setData(myMessageBuilder.build().toByteArray());
        //广播
        for (Integer playerId:players){
            ConcurrentHashMap<Integer,Channel> cMap=MmoCache.getInstance().getChannelConcurrentHashMap();
            Channel c=cMap.get(playerId);
            if (c!=null){
                c.writeAndFlush(nettyResponse);
            }
        }
        return  nettyResponse;
    }

    public void npcAttack(Integer npcId,Integer roleId){
        ScheduledFuture<?> t=ScheduledThreadPoolUtil.getNpcTaskMap().get(npcId);
        if (t!=null){
            //代表着该npc正在攻击一个目标
            return;
        }else{
            ScheduledThreadPoolUtil.NpcAttackTask npcAttackTask=new ScheduledThreadPoolUtil.NpcAttackTask(roleId,npcId);
            t=ScheduledThreadPoolUtil.getScheduledExecutorService().scheduleAtFixedRate(npcAttackTask,0,6,TimeUnit.SECONDS);
            ScheduledThreadPoolUtil.getNpcTaskMap().put(npcId,t);
        }
    }
}

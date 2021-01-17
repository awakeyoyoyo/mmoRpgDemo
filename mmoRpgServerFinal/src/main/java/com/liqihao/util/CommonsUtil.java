package com.liqihao.util;


import com.liqihao.Cache.*;
import com.liqihao.commons.ConstantValue;
import com.liqihao.commons.NettyResponse;
import com.liqihao.commons.RpgServerException;
import com.liqihao.commons.StateCode;
import com.liqihao.commons.enums.*;
import com.liqihao.dao.*;
import com.liqihao.pojo.*;
import com.liqihao.pojo.baseMessage.*;
import com.liqihao.pojo.bean.*;
import com.liqihao.pojo.bean.articleBean.EquipmentBean;
import com.liqihao.pojo.bean.articleBean.MedicineBean;
import com.liqihao.pojo.bean.bufferBean.BaseBufferBean;
import com.liqihao.pojo.bean.guildBean.GuildApplyBean;
import com.liqihao.pojo.bean.guildBean.GuildBean;
import com.liqihao.pojo.bean.guildBean.GuildRoleBean;
import com.liqihao.pojo.bean.guildBean.WareHouseManager;
import com.liqihao.pojo.bean.roleBean.BossBean;
import com.liqihao.pojo.bean.roleBean.MmoSimpleNPC;
import com.liqihao.pojo.bean.roleBean.MmoSimpleRole;
import com.liqihao.pojo.bean.roleBean.Role;
import com.liqihao.pojo.dto.ArticleDto;
import com.liqihao.pojo.dto.EquipmentDto;
import com.liqihao.protobufObject.CopySceneModel;
import com.liqihao.protobufObject.EmailModel;
import com.liqihao.protobufObject.GuildModel;
import com.liqihao.protobufObject.SceneModel;
import com.liqihao.provider.CopySceneProvider;
import com.liqihao.provider.EmailServiceProvider;
import com.liqihao.provider.GuildServiceProvider;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 字符串处理类
 * @author lqhao
 */
@Component
public class CommonsUtil implements ApplicationContextAware {
    private static ApplicationContext applicationContext;
    private static MmoBagPOJOMapper mmoBagPOJOMapper;
    private static MmoEquipmentPOJOMapper mmoEquipmentPOJOMapper;
    private static MmoEquipmentBagPOJOMapper equipmentBagPOJOMapper;
    private static MmoRolePOJOMapper mmoRolePOJOMapper;
    private static MmoEmailPOJOMapper mmoEmailPOJOMapper;
    private static MmoGuildApplyPOJOMapper mmoGuildApplyPOJOMapper;
    private static MmoGuildRolePOJOMapper mmoGuildRolePOJOMapper;
    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        applicationContext = context;
        mmoBagPOJOMapper=(MmoBagPOJOMapper)context.getBean("mmoBagPOJOMapper");
        mmoEquipmentPOJOMapper=(MmoEquipmentPOJOMapper)context.getBean("mmoEquipmentPOJOMapper");
        equipmentBagPOJOMapper=(MmoEquipmentBagPOJOMapper)context.getBean("mmoEquipmentBagPOJOMapper");
        mmoRolePOJOMapper=(MmoRolePOJOMapper)context.getBean("mmoRolePOJOMapper");
        mmoEmailPOJOMapper=(MmoEmailPOJOMapper)context.getBean("mmoEmailPOJOMapper");
        mmoGuildApplyPOJOMapper=(MmoGuildApplyPOJOMapper) context.getBean("mmoGuildApplyPOJOMapper");;
        mmoGuildRolePOJOMapper=(MmoGuildRolePOJOMapper) context.getBean("mmoGuildRolePOJOMapper");;
    }

    public static GuildBean MmoGuildPOJOToGuildBean(MmoGuildPOJO mmoGuildPOJO) {
        GuildBean guildBean=new GuildBean();
        guildBean.setId(mmoGuildPOJO.getId());
        guildBean.setName(mmoGuildPOJO.getName());
        guildBean.setLevel(mmoGuildPOJO.getLevel());
        guildBean.setPeopleNum(mmoGuildPOJO.getPeopleNum());
        guildBean.setCreateTime(mmoGuildPOJO.getCreateTime());
        guildBean.setChairmanId(mmoGuildPOJO.getChairmanId());
        List<MmoGuildApplyPOJO> guildApplyPOJOS=mmoGuildApplyPOJOMapper.selectByGuildId(guildBean.getId());
        List<GuildApplyBean> guildApplyBeans=new ArrayList<>();
        for (MmoGuildApplyPOJO guildApplyPOJO:guildApplyPOJOS) {
            GuildApplyBean guildApplyBean=CommonsUtil.guildApplyPOJOToGuildApplyBean(guildApplyPOJO);
            guildApplyBeans.add(guildApplyBean);
        }
        guildBean.getGuildApplyBeans().addAll(guildApplyBeans);
        List<MmoGuildRolePOJO> guildRolePOJOS=mmoGuildRolePOJOMapper.selectByGuildId(guildBean.getId());
        List<GuildRoleBean> guildRoleBeans=new ArrayList<>();
        for (MmoGuildRolePOJO guildRolePOJO:guildRolePOJOS) {
            GuildRoleBean guildRoleBean=CommonsUtil.guildRolePOJOToGuildRoleBean(guildRolePOJO);
            guildRoleBeans.add(guildRoleBean);
        }
        guildBean.getGuildRoleBeans().addAll(guildRoleBeans);
        // todo 公会仓库
        guildBean.setWareHouseManager(new WareHouseManager());
        return guildBean;
    }

    private static GuildRoleBean guildRolePOJOToGuildRoleBean(MmoGuildRolePOJO guildRolePOJO) {
        GuildRoleBean guildRoleBean=new GuildRoleBean();
        guildRoleBean.setId(guildRolePOJO.getId());
        guildRoleBean.setContribution(guildRolePOJO.getContribution());
        guildRoleBean.setGuildPositionId(guildRolePOJO.getGuildPositionId());
        guildRoleBean.setGuildId(guildRolePOJO.getGuildId());
        guildRoleBean.setRoleId(guildRolePOJO.getRoleId());
        return guildRoleBean;
    }

    private static GuildApplyBean guildApplyPOJOToGuildApplyBean(MmoGuildApplyPOJO guildApplyPOJO) {
        GuildApplyBean guildApplyBean=new GuildApplyBean();
        guildApplyBean.setId(guildApplyPOJO.getId());
        guildApplyBean.setGuildId(guildApplyPOJO.getGuildId());
        guildApplyBean.setCreateTime(guildApplyPOJO.getCreateTime());
        guildApplyBean.setEndTime(guildApplyPOJO.getEndTime());
        guildApplyBean.setRoleId(guildApplyPOJO.getRoleId());
        return guildApplyBean;
    }

    public static GuildModel.GuildPeopleDto guildRoleBeanToGuildPeopleDto(GuildRoleBean guildRoleBean) {
        GuildModel.GuildPeopleDto.Builder guildPeopleDtoBuilder=GuildModel.GuildPeopleDto.newBuilder();
        guildPeopleDtoBuilder.setRoleId(guildRoleBean.getRoleId()).setGuildPosition(guildRoleBean.getGuildPositionId())
                .setContribution(guildRoleBean.getContribution());
        MmoSimpleRole role=OnlineRoleMessageCache.getInstance().get(guildRoleBean.getRoleId());
        if(role==null){
            MmoRolePOJO mmoRolePOJO=RoleMessageCache.getInstance().get(guildRoleBean.getRoleId());
            guildPeopleDtoBuilder.setProfessionId(mmoRolePOJO.getProfessionId()).setOnStatus(RoleOnStatusCode.EXIT.getCode()).setName(mmoRolePOJO.getName());
        }else{
            guildPeopleDtoBuilder.setProfessionId(role.getProfessionId()).setOnStatus(role.getOnStatus()).setName(role.getName());
        }
        return guildPeopleDtoBuilder.build();
    }


    public static CopySceneModel.BossBeanDto bossBeanToBossBeanDto(BossBean boss) {
        CopySceneModel.BossBeanDto.Builder bossDtoBuilder=CopySceneModel.BossBeanDto.newBuilder();
        bossDtoBuilder.setId(boss.getId());
        bossDtoBuilder.setAttack(boss.getAttack());
        bossDtoBuilder.setMp(boss.getMp());
        bossDtoBuilder.setName(boss.getName());
        bossDtoBuilder.setNowBlood(boss.getNowHp());
        bossDtoBuilder.setNowMp(boss.getNowMp());
        bossDtoBuilder.setStatus(boss.getStatus());
        bossDtoBuilder.setBlood(boss.getHp());
        List<CopySceneModel.BufferDto> bufferDtoList=new ArrayList<>();
        if (boss.getBufferBeans().size()>0){
            for (BaseBufferBean b:boss.getBufferBeans()) {
                CopySceneModel.BufferDto bufferDto= bufferBeanToBufferDto(b);
                bufferDtoList.add(bufferDto);
            }
        }
        bossDtoBuilder.addAllBufferDtos(bufferDtoList);
        return  bossDtoBuilder.build();
    }

    private static CopySceneModel.BufferDto bufferBeanToBufferDto(BaseBufferBean b) {
        CopySceneModel.BufferDto.Builder bufferDtoBuilder=CopySceneModel.BufferDto.newBuilder();
        BufferMessage bufferMessage= BufferMessageCache.getInstance().get(b.getBufferMessageId());
        return bufferDtoBuilder.setId(bufferMessage.getId()).setName(bufferMessage.getName()).setFromRoleId(b.getFromRoleId())
                .setToRoleId(b.getToRoleId()).setCreateTime(b.getCreateTime()).setLastTime(bufferMessage.getLastTime())
                .build();
    }

    public static CopySceneModel.RoleDto mmoSimpleRolesToCopySceneRoleDto(MmoSimpleRole role) {
        CopySceneModel.RoleDto.Builder roleDtoBuilder=CopySceneModel.RoleDto.newBuilder();
        List<CopySceneModel.BufferDto> bufferDtoList=new ArrayList<>();
        if (role.getBufferBeans().size()>0){
            for (BaseBufferBean b:role.getBufferBeans()) {
                CopySceneModel.BufferDto bufferDto= bufferBeanToBufferDto(b);
                bufferDtoList.add(bufferDto);
            }
        }
        return roleDtoBuilder.setId(role.getId()).setBlood(role.getHp()).setNowBlood(role.getNowHp())
                .setNowMp(role.getNowMp()).addAllBufferDtos(bufferDtoList).setOnStatus(role.getOnStatus())
                .setTeamId(role.getTeamId()).setStatus(role.getStatus())
                .setName(role.getName()).setMp(role.getMp()).setType(role.getType()).build();

    }

    public static MmoEmailBean emailPOJOToMmoEmailBean(MmoEmailPOJO m) {
        MmoEmailBean mmoEmailBean=new MmoEmailBean();
        mmoEmailBean.setId(m.getId());
        mmoEmailBean.setArticleNum(m.getArticleNum());
        mmoEmailBean.setArticleMessageId(m.getArticleMessageId());
        mmoEmailBean.setArticleType(m.getArticleType());
        mmoEmailBean.setTitle(m.getTitle());
        mmoEmailBean.setContext(m.getContext());
        mmoEmailBean.setHasArticle(m.getArticleMessageId()!=null);
        mmoEmailBean.setCreateTime(m.getCreateTime());
        mmoEmailBean.setFromRoleId(m.getFromRoleId());
        mmoEmailBean.setIntoDataBase(true);
        mmoEmailBean.setToDelete(m.getToDelete());
        mmoEmailBean.setFromDelete(m.getFromDelete());
        mmoEmailBean.setChecked(m.getChecked());
        mmoEmailBean.setToRoleId(m.getToRoleId());
        mmoEmailBean.setGet(m.getIsGet());
        return mmoEmailBean;
    }

    public static EmailModel.EmailDto mmoEmailBeanToEmailDto(MmoEmailBean mmoEmailBean) {
        EmailModel.EmailDto emailDto=EmailModel.EmailDto.newBuilder()
                .setArticleMessageId(mmoEmailBean.getArticleMessageId()).setArticleNum(mmoEmailBean.getArticleNum()).setArticleType(mmoEmailBean.getArticleType())
                .setChecked(mmoEmailBean.getChecked()).setContext(mmoEmailBean.getContext()).setCreateTime(mmoEmailBean.getCreateTime())
                .setId(mmoEmailBean.getId()).setFromRoleId(mmoEmailBean.getFromRoleId()).setToRoleId(mmoEmailBean.getToRoleId())
                .setTitle(mmoEmailBean.getTitle()).setIsGet(mmoEmailBean.getGet()).setHasArticle(mmoEmailBean.getHasArticle()).build();
        return emailDto;
    }

    public static EmailModel.EmailSimpleDto mmoEmailBeanToEmailSimpleDto(MmoEmailBean mmoEmailBean) {
        EmailModel.EmailSimpleDto emailDto=EmailModel.EmailSimpleDto.newBuilder()
                .setChecked(mmoEmailBean.getChecked()).setContext(mmoEmailBean.getContext()).setCreateTime(mmoEmailBean.getCreateTime())
                .setId(mmoEmailBean.getId()).setFromRoleId(mmoEmailBean.getFromRoleId()).setToRoleId(mmoEmailBean.getToRoleId())
                .setTitle(mmoEmailBean.getTitle()).setHasArticle(mmoEmailBean.getHasArticle()).build();
        return emailDto;
    }

    public static GoodsBean goodMessageToGoodBean(GoodsMessage g) {
        GoodsBean goodsBean=new GoodsBean();
        goodsBean.setId(g.getId());
        goodsBean.setNowNum(g.getNum());
        goodsBean.setGoodsMessageId(g.getArticleMessageId());
        return goodsBean;
    }

    public static GuildBean mmoGuildPOJOToGuildBean(MmoGuildPOJO mmoGuildPOJO) {
        GuildBean guildBean=new GuildBean();
        guildBean.setId(mmoGuildPOJO.getId());
        guildBean.setLevel(mmoGuildPOJO.getLevel());
        guildBean.setPeopleNum(mmoGuildPOJO.getPeopleNum());
        guildBean.setChairmanId(mmoGuildPOJO.getChairmanId());
        guildBean.setMoney(mmoGuildPOJO.getMoney());
        Integer wareHouseSize=MmoBaseMessageCache.getInstance().getGuildBaseMessage().getMaxWareHouseNumber();
        guildBean.setWareHouseManager(new WareHouseManager(wareHouseSize));
        guildBean.setCreateTime(mmoGuildPOJO.getCreateTime());
        guildBean.setName(mmoGuildPOJO.getName());
        return guildBean;
    }

    public static GuildModel.GuildApplyDto guildApplyBeanToGuildApplyDto(GuildApplyBean guildApplyBean) {
        GuildModel.GuildApplyDto.Builder guildApplyDtoBuilder=GuildModel.GuildApplyDto.newBuilder();
        GuildBean guildBean= GuildServiceProvider.getInstance().getGuildBeanById(guildApplyBean.getGuildId());
        MmoRolePOJO mmoRolePOJO=RoleMessageCache.getInstance().get(guildApplyBean.getRoleId());
        guildApplyDtoBuilder.setGuildId(guildApplyBean.getGuildId()).setGuildName(guildBean.getName())
                .setId(guildApplyBean.getId()).setRoleId(guildApplyBean.getRoleId()).setRoleName(mmoRolePOJO.getName())
                .setEndTime(guildApplyBean.getEndTime()).setCreateTime(guildApplyBean.getCreateTime());
        return guildApplyDtoBuilder.build();
    }

    public static CopySceneBean copySceneMessageToCopySceneBean(CopySceneMessage copySceneMessage) {
        CopySceneBean copySceneBean=new CopySceneBean();
        copySceneBean.setCreateTime(System.currentTimeMillis());
        copySceneBean.setEndTime(System.currentTimeMillis()+1000*copySceneMessage.getLastTime());
        copySceneBean.setRoles(new CopyOnWriteArrayList<>());
        copySceneBean.setStatus(CopySceneStatusCode.ON_DOING.getCode());
        copySceneBean.setCopySceneMessageId(copySceneMessage.getId());
        return copySceneBean;
    }

    public static BossBean bossMessageToBossBean(BossMessage bossMessage) {
        BossBean bossBean=new BossBean();
        bossBean.setBufferBeans(new CopyOnWriteArrayList<BaseBufferBean>());
        bossBean.setCdMap(new HashMap<>());
        bossBean.setHatredMap(new ConcurrentHashMap<>());
        bossBean.setHp(bossMessage.getBlood());
        bossBean.setNowMp(bossMessage.getMp());
        bossBean.setStatus(RoleStatusCode.ALIVE.getCode());
        bossBean.setOnStatus(RoleOnStatusCode.ONLINE.getCode());
        bossBean.setType(RoleTypeCode.ENEMY.getCode());
        bossBean.setAttack(bossMessage.getAttack());
        bossBean.setNowHp(bossMessage.getBlood());
        bossBean.setDamageAdd(bossMessage.getDamageAdd());
        bossBean.setId(bossMessage.getId());
        bossBean.setBossMessageId(bossMessage.getId());
        bossBean.setMp(bossMessage.getMp());
        bossBean.setName(bossMessage.getName());
        return bossBean;
    }

    /**
     * 根据channle获取线程池线程下表
     */
    public static Integer getIndexByChannel(Channel channel) {
        int threadSize = LogicThreadPool.getInstance().getThreadSize();
        Integer index = channel.hashCode() & (threadSize - 1);
        return index;
    }

    /**
     * 判断是否登陆
     * @param channel
     * @return
     */
    public static MmoSimpleRole checkLogin(Channel channel) throws Exception {
        MmoSimpleRole mmoSimpleRole= CommonsUtil.getRoleByChannel(channel);
        if (mmoSimpleRole==null){
            throw new RpgServerException(StateCode.FAIL,"用户未登录");

        }
        return mmoSimpleRole;
    }


    /**
     * 根据channel获取role
     * @param channel
     * @return
     */
    public static MmoSimpleRole getRoleByChannel(Channel channel){
        AttributeKey<MmoSimpleRole> key = AttributeKey.valueOf("role");
        if (channel.hasAttr(key) && channel.attr(key).get() != null)
        {
            return channel.attr(key).get();
        }
        return null;
    }

    /**
     * 药物信息类转化为药物bean
     * @param medicineMessage
     * @return
     */
    public static MedicineBean medicineMessageToMedicineBean(MedicineMessage medicineMessage){
        MedicineBean bean=new MedicineBean();
        bean.setMedicineMessageId(medicineMessage.getId());
        return bean;
    }

    /**
     * 装备信息转化为装备bean
     * @param equipmentMessage
     * @return
     */
    public static EquipmentBean equipmentMessageToEquipmentBean(EquipmentMessage equipmentMessage){
        EquipmentBean bean=new EquipmentBean();
        bean.setArticleId(null);
        bean.setNowDurability(equipmentMessage.getDurability());
        bean.setQuantity(1);
        bean.setEquipmentMessageId(equipmentMessage.getId());
        bean.setEquipmentMessageId(equipmentMessage.getId());
        return bean;
    }

    public static MmoSimpleRole NpcToMmoSimpleRole(MmoSimpleNPC npc) {
        MmoSimpleRole roleTemp = new MmoSimpleRole();
        roleTemp.setId(npc.getId());
        roleTemp.setName(npc.getName());
        roleTemp.setMmoSceneId(npc.getMmoSceneId());
        roleTemp.setStatus(npc.getStatus());
        roleTemp.setType(npc.getType());
        roleTemp.setOnStatus(npc.getOnStatus());
        roleTemp.setHp(npc.getHp());
        roleTemp.setNowHp(npc.getNowHp());
        roleTemp.setMp(npc.getMp());
        roleTemp.setAttack(npc.getAttack());
        roleTemp.setNowMp(npc.getNowMp());
        return roleTemp;
    }

    /**
     * 分割字符串返回id集合
     * @param str
     * @return
     */
    public  static List<Integer> split(String str) {
        if (null == str || str.trim().length() == 0) {
            return new ArrayList<Integer>();
        }
        String[] strings=str.split(",");
        List<Integer> res=new ArrayList<>();
        for (String s:strings){
            if (s.isEmpty()){
                continue;
            }
            Integer i=Integer.parseInt(s);
            res.add(i);
        }
        return res;
    }

    /**
     * 分割字符串返回id集合
     * @param str
     * @return
     */
    public  static List<String> splitToStringList(String str) {
        if (null == str || str.trim().length() == 0) {
            return new ArrayList<String>();
        }
        String[] strings=str.split("-");
        return Arrays.asList(strings);
    }

    public static String listToString(List<Integer> oldRoles) {
        StringBuffer stringBuffer=new StringBuffer();
        if (oldRoles.size()==0) {
            return "";
        }
        for (int i=0;i<oldRoles.size();i++){
            Integer id=oldRoles.get(i);
            if (i==oldRoles.size()-1){
                stringBuffer.append(id);
            }else {
                stringBuffer.append(id).append(",");
            }
        }
        return stringBuffer.toString();
    }

    public static SceneBean sceneMessageToSceneBean(SceneMessage m) {
        SceneBean sceneBean=new SceneBean();
        sceneBean.setId(m.getId());
        sceneBean.setName(m.getPlaceName());
        sceneBean.setCanScenes(split(m.getCanScene()));
        sceneBean.setRoles(new ArrayList<>());
        sceneBean.setHelperBeans(new ArrayList<>());
        List<Integer> npcs=new ArrayList<>();
        for (MmoSimpleNPC mpc: NpcMessageCache.getInstance().values()) {
            if (mpc.getMmoSceneId().equals(m.getId())){
                npcs.add(mpc.getId());
            }
        }
        sceneBean.setNpcs(npcs);
        return sceneBean;
    }

    /**
     * 根据技能ids获取技能beans
     * @param skillIds
     * @return
     */
    public static List<SkillBean> skillIdsToSkillBeans(List<Integer> skillIds) {
        List<SkillBean> list=new ArrayList<>();
        for (Integer id:skillIds) {
            SkillMessage message= SkillMessageCache.getInstance().get(id);
            SkillBean skillBean=new SkillBean();
            skillBean.setId(message.getId());
            skillBean.setSkillType(message.getSkillType());
            skillBean.setAddPerson(message.getAddPerson());
            skillBean.setConsumeType(message.getConsumeType());
            skillBean.setSkillName(message.getSkillName());
            skillBean.setSkillAttackType(message.getSkillAttackType());
            skillBean.setBaseDamage(message.getBaseDamage());
            skillBean.setConsumeNum(message.getConsumeNum());
            skillBean.setCd(message.getCd());
            skillBean.setChantTime(message.getChantTime());
            skillBean.setSkillDamageType(message.getSkillDamageType());
            skillBean.setBufferIds(split(message.getBufferIds()));
            list.add(skillBean);
        }
        return list;
    }

    /**
     * 向场景仲角色发送人物登场
     */
    public static void sendRoleResponse(List<Role> newRoles, Integer sceneId, Integer copySceneId){
        //protobuf
        SceneModel.SceneModelMessage.Builder messageDataBuilder = SceneModel.SceneModelMessage.newBuilder();
        messageDataBuilder.setDataType(SceneModel.SceneModelMessage.DateType.RoleResponse);
        SceneModel.RoleResponse.Builder roleResponseBuilder = SceneModel.RoleResponse.newBuilder();
        List<SceneModel.RoleDTO> roleDTOS = new ArrayList<>();
        for (Role m : newRoles) {
            SceneModel.RoleDTO.Builder msr = SceneModel.RoleDTO.newBuilder().setId(m.getId())
                    .setName(m.getName())
                    .setOnStatus(m.getOnStatus())
                    .setStatus(m.getStatus())
                    .setType(m.getType())
                    .setBlood(m.getHp())
                    .setNowBlood(m.getNowHp())
                    .setMp(m.getMp())
                    .setTeamId(m.getTeamId() == null ? -1 : m.getTeamId())
                    .setNowMp(m.getNowMp())
                    .setAttack(m.getAttack())
                    .setAttackAdd(m.getDamageAdd());
            if (m.getType().equals(RoleTypeCode.PLAYER.getCode())) {
                MmoSimpleRole mmoSimpleRole1 = (MmoSimpleRole) m;
                msr.setProfessionId(mmoSimpleRole1.getProfessionId());
            }
            roleDTOS.add(msr.build());
        }
        roleResponseBuilder.addAllRoleDtos(roleDTOS);
        messageDataBuilder.setRoleResponse(roleResponseBuilder.build());
        byte[] data2 = messageDataBuilder.build().toByteArray();
        NettyResponse nettyResponse = new NettyResponse();
        nettyResponse.setCmd(ConstantValue.ROLE_RESPONSE);
        nettyResponse.setStateCode(200);
        nettyResponse.setData(data2);
        List<Integer> players;
        if (sceneId!=null) {
            players = SceneBeanMessageCache.getInstance().get(sceneId).getRoles();
            for (Integer playerId:players){
                Channel c= ChannelMessageCache.getInstance().get(playerId);
                if (c!=null){
                    c.writeAndFlush(nettyResponse);
                }
            }

        }else{
            List<Role> roles = CopySceneProvider.getCopySceneBeanById(copySceneId).getRoles();
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
}

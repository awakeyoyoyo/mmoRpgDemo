package com.liqihao.util;


import com.googlecode.protobuf.format.JsonFormat;
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
import com.liqihao.pojo.bean.taskBean.BaseTaskBean;
import com.liqihao.pojo.bean.articleBean.EquipmentBean;
import com.liqihao.pojo.bean.articleBean.MedicineBean;
import com.liqihao.pojo.bean.buffBean.BaseBuffBean;
import com.liqihao.pojo.bean.dealBankBean.DealBankArticleBean;
import com.liqihao.pojo.bean.dealBankBean.DealBankAuctionBean;
import com.liqihao.pojo.bean.guildBean.GuildApplyBean;
import com.liqihao.pojo.bean.guildBean.GuildBean;
import com.liqihao.pojo.bean.guildBean.GuildRoleBean;
import com.liqihao.pojo.bean.guildBean.WareHouseManager;
import com.liqihao.pojo.bean.roleBean.*;
import com.liqihao.pojo.dto.ArticleDto;
import com.liqihao.protobufObject.*;
import com.liqihao.provider.CopySceneProvider;
import com.liqihao.provider.DealBankServiceProvider;
import com.liqihao.provider.GuildServiceProvider;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
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
public class CommonsUtil {
    private static ApplicationContext applicationContext;
    private static MmoBagPOJOMapper mmoBagPOJOMapper;
    private static MmoEquipmentPOJOMapper mmoEquipmentPOJOMapper;
    private static MmoEquipmentBagPOJOMapper equipmentBagPOJOMapper;
    private static MmoRolePOJOMapper mmoRolePOJOMapper;
    private static MmoEmailPOJOMapper mmoEmailPOJOMapper;
    private static MmoGuildApplyPOJOMapper mmoGuildApplyPOJOMapper;
    private static MmoGuildRolePOJOMapper mmoGuildRolePOJOMapper;
    private static MmoWareHousePOJOMapper mmoWareHousePOJOMapper;
    private static MmoDealBankAuctionPOJOMapper mmoDealBankAuctionPOJOMapper;

    public static PlayModel.RoleDTO mmoRoleToPlayModelRoleDto(MmoSimpleRole simpleRole) {
        PlayModel.RoleDTO.Builder mmoSimpleRoleBuilder = PlayModel.RoleDTO.newBuilder();
        mmoSimpleRoleBuilder.setId(simpleRole.getId())
                .setName(simpleRole.getName())
                .setOnStatus(simpleRole.getOnStatus())
                .setStatus(simpleRole.getStatus())
                .setType(simpleRole.getType())
                .setBlood(simpleRole.getHp())
                .setNowBlood(simpleRole.getHp())
                .addAllSkillIdList(simpleRole.getSkillIdList())
                .setMp(simpleRole.getMp())
                .setSceneId(simpleRole.getMmoSceneId())
                .setNowMp(simpleRole.getNowMp())
                .setTeamId(simpleRole.getTeamId() == null ? -1 : simpleRole.getTeamId())
                .setAttack(simpleRole.getAttack())
                .setAttackAdd(simpleRole.getDamageAdd())
                .setLevel(simpleRole.getLevel())
                .setEquipmentLevel(simpleRole.getEquipmentLevel())
                .setMoney(simpleRole.getMoney())
                .setProfessionId(simpleRole.getProfessionId())
                .setGuildName(simpleRole.getGuildBean()==null?"":simpleRole.getGuildBean().getName())
                .setGuildId(simpleRole.getGuildBean()==null?-1:simpleRole.getGuildBean().getId())
                .build();
        return mmoSimpleRoleBuilder.build();
    }

    public static List<Role> getAllRolesFromScene(MmoSimpleRole simpleRole) {
        List<Role> sceneRoles = new ArrayList<>();
        SceneBean sceneBean = SceneBeanMessageCache.getInstance().get(simpleRole.getMmoSceneId());
        //NPC
        for (Integer id : sceneBean.getNpcs()) {
            MmoSimpleNPC temp = NpcMessageCache.getInstance().get(id);
            sceneRoles.add(temp);
        }
        //ROLES
        for (Integer id : sceneBean.getRoles()) {
            MmoSimpleRole temp = OnlineRoleMessageCache.getInstance().get(id);
            sceneRoles.add(temp);
        }
        //helper
        if (sceneBean.getHelperBeans().size() > 0) {
            sceneRoles.addAll(sceneBean.getHelperBeans());
        }
        return sceneRoles;
    }


    public static void sendUpLevelAllRoles(Integer addLevel, Role role) {
        NettyResponse nettyResponse=new NettyResponse();
        PlayModel.PlayModelMessage.Builder messageData = PlayModel.PlayModelMessage.newBuilder();
        messageData.setDataType(PlayModel.PlayModelMessage.DateType.UpLevelResponse);
        PlayModel.UpLevelResponse.Builder registerResponseBuilder = PlayModel.UpLevelResponse.newBuilder();
        registerResponseBuilder.setLevel(role.getLevel()).setRoleId(role.getId()).setRoleName(role.getName())
                .setAddLevel(addLevel);
        messageData.setUpLevelResponse(registerResponseBuilder.build());
        nettyResponse.setCmd(ConstantValue.UP_LEVEL_RESPONSE);
        nettyResponse.setData(messageData.build().toByteArray());
        MmoSimpleRole mmoSimpleRole= (MmoSimpleRole) role;
        String json= JsonFormat.printToString(messageData.build());
        NotificationUtil.notificationSceneRole(nettyResponse,mmoSimpleRole,json);
    }

    public static SceneModel.RoleDTO.Builder roleToSceneModelRoleDto(Role mmoRole) {
        SceneModel.RoleDTO.Builder msr = SceneModel.RoleDTO.newBuilder();
        msr.setId(mmoRole.getId());
        msr.setName(mmoRole.getName());
        msr.setType(mmoRole.getType());
        msr.setStatus(mmoRole.getStatus());
        msr.setOnStatus(mmoRole.getOnStatus());
        msr.setBlood(mmoRole.getHp());
        msr.setNowBlood(mmoRole.getNowHp());
        msr.setLevel(mmoRole.getLevel());
        msr.setEquipmentLevel(mmoRole.getEquipmentLevel());
        msr.setMp(mmoRole.getMp());
        msr.setNowMp(mmoRole.getNowMp());
        msr.setTeamId(mmoRole.getTeamId() == null ? -1 : mmoRole.getTeamId());
        msr.setAttack(mmoRole.getAttack());
        msr.setAttackAdd(mmoRole.getDamageAdd());
        if (mmoRole.getType().equals(RoleTypeCode.PLAYER.getCode())) {
            MmoSimpleRole r = (MmoSimpleRole) mmoRole;
            msr.setProfessionId(r.getProfessionId());
            msr.setGuildName(r.getGuildBean()==null?"":r.getGuildBean().getName());
            msr.setGuildId(r.getGuildBean()==null?-1:r.getGuildBean().getId());
        }
        return msr;
    }

    public static SkillBean skillMessageToSkillBean(SkillMessage skillMessage) {
        SkillBean skillBean = new SkillBean();
        skillBean.setId(skillMessage.getId());
        skillBean.setConsumeType(skillMessage.getConsumeType());
        skillBean.setConsumeNum(skillMessage.getConsumeNum());
        skillBean.setCd(skillMessage.getCd());
        skillBean.setBufferIds(CommonsUtil.split(skillMessage.getBufferIds()));
        skillBean.setBaseDamage(skillMessage.getBaseDamage());
        skillBean.setSkillName(skillMessage.getSkillName());
        skillBean.setSkillAttackType(skillMessage.getSkillAttackType());
        skillBean.setSkillDamageType(skillMessage.getSkillDamageType());
        skillBean.setChantTime(skillMessage.getChantTime());
        skillBean.setAddPerson(skillMessage.getAddPerson());
        skillBean.setSkillType(skillMessage.getSkillType());
        return skillBean;
    }


    @Autowired
    public  void setMmoBagPOJOMapper(MmoBagPOJOMapper mmoBagPOJOMapper) {
        CommonsUtil.mmoBagPOJOMapper = mmoBagPOJOMapper;
    }
    @Autowired

    public  void setMmoEquipmentPOJOMapper(MmoEquipmentPOJOMapper mmoEquipmentPOJOMapper) {
        CommonsUtil.mmoEquipmentPOJOMapper = mmoEquipmentPOJOMapper;
    }
    @Autowired

    public  void setEquipmentBagPOJOMapper(MmoEquipmentBagPOJOMapper equipmentBagPOJOMapper) {
        CommonsUtil.equipmentBagPOJOMapper = equipmentBagPOJOMapper;
    }
    @Autowired

    public  void setMmoRolePOJOMapper(MmoRolePOJOMapper mmoRolePOJOMapper) {
        CommonsUtil.mmoRolePOJOMapper = mmoRolePOJOMapper;
    }
    @Autowired

    public  void setMmoEmailPOJOMapper(MmoEmailPOJOMapper mmoEmailPOJOMapper) {
        CommonsUtil.mmoEmailPOJOMapper = mmoEmailPOJOMapper;
    }
    @Autowired

    public  void setMmoGuildApplyPOJOMapper(MmoGuildApplyPOJOMapper mmoGuildApplyPOJOMapper) {
        CommonsUtil.mmoGuildApplyPOJOMapper = mmoGuildApplyPOJOMapper;
    }
    @Autowired

    public  void setMmoGuildRolePOJOMapper(MmoGuildRolePOJOMapper mmoGuildRolePOJOMapper) {
        CommonsUtil.mmoGuildRolePOJOMapper = mmoGuildRolePOJOMapper;
    }
    @Autowired

    public  void setMmoWareHousePOJOMapper(MmoWareHousePOJOMapper mmoWareHousePOJOMapper) {
        CommonsUtil.mmoWareHousePOJOMapper = mmoWareHousePOJOMapper;
    }
    @Autowired

    public  void setMmoDealBankAuctionPOJOMapper(MmoDealBankAuctionPOJOMapper mmoDealBankAuctionPOJOMapper) {
        CommonsUtil.mmoDealBankAuctionPOJOMapper = mmoDealBankAuctionPOJOMapper;
    }


    /**
     * BankArticlePOJO转化为DealBankArticleBean
     * @param dealBankArticlePOJO
     * @return
     */
    public static DealBankArticleBean dealBankArticlePOJOToDealBankArticleBean(MmoDealBankArticlePOJO dealBankArticlePOJO) {
        DealBankArticleBean dealBankArticleBean=new DealBankArticleBean();
        dealBankArticleBean.setDealBankArticleDbId(dealBankArticlePOJO.getId());
        dealBankArticleBean.setArticleType(dealBankArticlePOJO.getArticleType());
        dealBankArticleBean.setArticleMessageId(dealBankArticlePOJO.getArticleMessageId());
        dealBankArticleBean.setNum(dealBankArticlePOJO.getNum());
        dealBankArticleBean.setType(dealBankArticlePOJO.getType());
        dealBankArticleBean.setFromRoleId(dealBankArticlePOJO.getFromRoleId());
        dealBankArticleBean.setToRoleId(dealBankArticlePOJO.getToRoleId());
        dealBankArticleBean.setCreateTime(dealBankArticlePOJO.getCreateTime());
        dealBankArticleBean.setEndTime(dealBankArticlePOJO.getEndTime());
        dealBankArticleBean.setPrice(dealBankArticlePOJO.getPrice());
        dealBankArticleBean.setHighPrice(dealBankArticlePOJO.getHighPrice());
        dealBankArticleBean.setEquipmentId(dealBankArticlePOJO.getEquipmentId());
        List<MmoDealBankAuctionPOJO> dealBankAuctionPOJOS=mmoDealBankAuctionPOJOMapper.selectAll();
        for (MmoDealBankAuctionPOJO dealBankAuctionPOJO : dealBankAuctionPOJOS) {
            DealBankAuctionBean d= CommonsUtil.dealDealBankAuctionPOJOToDealBankAuctionBean(dealBankAuctionPOJO);
            d.setDealBeanAuctionBeanId(DealBankServiceProvider.dealBankAuctionBeanIdAuto.incrementAndGet());
            dealBankArticleBean.getDealBankAuctionBeans().add(d);
        }
        return dealBankArticleBean;
    }

    /**
     * DealBankAuctionPOJO转化为DealBankAuctionBean
     * @param dealBankAuctionPOJO
     * @return
     */
    public static DealBankAuctionBean dealDealBankAuctionPOJOToDealBankAuctionBean(MmoDealBankAuctionPOJO dealBankAuctionPOJO) {
        DealBankAuctionBean dealBankAuctionBean=new DealBankAuctionBean();
        dealBankAuctionBean.setDealBeanAuctionBeanDbId(dealBankAuctionPOJO.getId());
        dealBankAuctionBean.setMoney(dealBankAuctionPOJO.getMoney());
        dealBankAuctionBean.setDealBeanArticleBeanDbId(dealBankAuctionBean.getDealBeanArticleBeanDbId());
        dealBankAuctionBean.setFromRoleId(dealBankAuctionPOJO.getFromRoleId());
        dealBankAuctionBean.setCreateTime(dealBankAuctionPOJO.getCreateTime());
        return dealBankAuctionBean;
    }

    public static TaskModel.TaskDto taskBeanToTaskDto(BaseTaskBean taskBean) {
        TaskModel.TaskDto taskDto=TaskModel.TaskDto.newBuilder().setTaskMessageId(taskBean.getTaskMessageId())
                .setCreateTime(taskBean.getCreateTime()).setProgress(taskBean.getProgress()).setStatus(taskBean.getStatus()).build();
        return taskDto;
    }

    public static DealBankModel.DealBankArticleDto dealBankArticleBeanToDealBankArticleDto(DealBankArticleBean dealBankArticleBean) {
        List<DealBankAuctionBean> dealBankAuctionBeans=dealBankArticleBean.getDealBankAuctionBeans();
        List<DealBankModel.DealBankAuctionDto> dealBankAuctionDtos=new ArrayList<>();
        for (DealBankAuctionBean dealBankAuctionBean : dealBankAuctionBeans) {
            DealBankModel.DealBankAuctionDto dealBankAuctionDto=dealBankAuctionBeanToDealBankAuctionDto(dealBankAuctionBean);
            dealBankAuctionDtos.add(dealBankAuctionDto);
        }
        DealBankModel.DealBankArticleDto dealBankArticleDto=DealBankModel.DealBankArticleDto.newBuilder()
                .addAllDealBankAuctionDtos(dealBankAuctionDtos).setDealBankArticleBeanId(dealBankArticleBean.getDealBeanArticleBeanId())
                .setArticleType(dealBankArticleBean.getArticleType()).setArticleMessageId(dealBankArticleBean.getArticleMessageId())
                .setNum(dealBankArticleBean.getNum()).setPrice(dealBankArticleBean.getPrice())
                .setHighPrice(dealBankArticleBean.getHighPrice()==null?0:dealBankArticleBean.getHighPrice()).setFromRoleId(dealBankArticleBean.getFromRoleId())
                .setToRoleId(dealBankArticleBean.getToRoleId()==null?-1:dealBankArticleBean.getToRoleId()).setType(dealBankArticleBean.getType())
                .setEndTime(dealBankArticleBean.getEndTime()).setCreateTime(dealBankArticleBean.getCreateTime())
                .setEquipmentId(dealBankArticleBean.getEquipmentId()==null?-1:dealBankArticleBean.getEquipmentId()).build();
        return dealBankArticleDto;
    }

    private static DealBankModel.DealBankAuctionDto dealBankAuctionBeanToDealBankAuctionDto(DealBankAuctionBean dealBankAuctionBean) {
        DealBankModel.DealBankAuctionDto dealBankAuctionDto=DealBankModel.DealBankAuctionDto.newBuilder()
                .setCreateTime(dealBankAuctionBean.getCreateTime())
                .setFromRoleId(dealBankAuctionBean.getFromRoleId())
                .setMoney(dealBankAuctionBean.getMoney())
                .setDealBeanAuctionBeanId(dealBankAuctionBean.getDealBeanAuctionBeanId()).build();
        return dealBankAuctionDto;
    }



    public static GuildBean MmoGuildPOJOToGuildBean(MmoGuildPOJO mmoGuildPOJO) {
        GuildBean guildBean=new GuildBean();
        guildBean.setId(mmoGuildPOJO.getId());
        guildBean.setName(mmoGuildPOJO.getName());
        guildBean.setLevel(mmoGuildPOJO.getLevel());
        guildBean.setPeopleNum(mmoGuildPOJO.getPeopleNum());
        guildBean.setCreateTime(mmoGuildPOJO.getCreateTime());
        guildBean.setChairmanId(mmoGuildPOJO.getChairmanId());
        guildBean.setMoney(mmoGuildPOJO.getMoney());
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
        Integer wareHouseSize=MmoBaseMessageCache.getInstance().getGuildBaseMessage().getMaxWareHouseNumber();
        guildBean.setWareHouseManager(new WareHouseManager(wareHouseSize));
        List<MmoWareHousePOJO> mmoWareHousePOJOS=mmoWareHousePOJOMapper.selectByGuildId(guildBean.getId());
        for (MmoWareHousePOJO mmoWareHousePOJO : mmoWareHousePOJOS) {
            if (mmoWareHousePOJO.getArticleType().equals(ArticleTypeCode.EQUIPMENT.getCode())) {
                MmoEquipmentPOJO mmoEquipmentPOJO = mmoEquipmentPOJOMapper.selectByPrimaryKey(mmoWareHousePOJO.getArticleMessageId());
                EquipmentMessage message = EquipmentMessageCache.getInstance().get(mmoEquipmentPOJO.getMessageId());
                EquipmentBean equipmentBean = CommonsUtil.equipmentMessageToEquipmentBean(message);
                equipmentBean.setQuantity(mmoWareHousePOJO.getNumber());
                equipmentBean.setEquipmentId(mmoEquipmentPOJO.getId());
                equipmentBean.setWareHouseDBId(mmoWareHousePOJO.getId());
                equipmentBean.setNowDurability(mmoEquipmentPOJO.getNowDurability());
                guildBean.getWareHouseManager().putFromDatabase(equipmentBean);
            } else if (mmoWareHousePOJO.getArticleType().equals(ArticleTypeCode.MEDICINE.getCode())) {
                MedicineMessage message = MedicineMessageCache.getInstance().get(mmoWareHousePOJO.getArticleMessageId());
                MedicineBean medicineBean = CommonsUtil.medicineMessageToMedicineBean(message);
                medicineBean.setQuantity(mmoWareHousePOJO.getNumber());
                medicineBean.setWareHouseDBId(mmoWareHousePOJO.getId());
                guildBean.getWareHouseManager().putFromDatabase(medicineBean);
            } else {
            }
        }
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
            for (BaseBuffBean b:boss.getBufferBeans()) {
                CopySceneModel.BufferDto bufferDto= bufferBeanToBufferDto(b);
                bufferDtoList.add(bufferDto);
            }
        }
        bossDtoBuilder.addAllBufferDtos(bufferDtoList);
        return  bossDtoBuilder.build();
    }

    private static CopySceneModel.BufferDto bufferBeanToBufferDto(BaseBuffBean b) {
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
            for (BaseBuffBean b:role.getBufferBeans()) {
                CopySceneModel.BufferDto bufferDto= bufferBeanToBufferDto(b);
                bufferDtoList.add(bufferDto);
            }
        }
        return roleDtoBuilder.setId(role.getId()).setBlood(role.getHp()).setNowBlood(role.getNowHp())
                .setNowMp(role.getNowMp()).addAllBufferDtos(bufferDtoList).setOnStatus(role.getOnStatus())
                .setTeamId(role.getTeamId()).setStatus(role.getStatus())
                .setName(role.getName()).setMp(role.getMp()).setType(role.getType()).build();

    }

    public static EmailBean emailPOJOToMmoEmailBean(MmoEmailPOJO m) {
        EmailBean mmoEmailBean=new EmailBean();
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
        mmoEmailBean.setGetFlag(m.getIsGet());
        return mmoEmailBean;
    }

    public static EmailModel.EmailDto mmoEmailBeanToEmailDto(EmailBean mmoEmailBean) {
        EmailModel.EmailDto emailDto=EmailModel.EmailDto.newBuilder()
                .setArticleMessageId(mmoEmailBean.getArticleMessageId()).setArticleNum(mmoEmailBean.getArticleNum()).setArticleType(mmoEmailBean.getArticleType())
                .setChecked(mmoEmailBean.getChecked()).setContext(mmoEmailBean.getContext()).setCreateTime(mmoEmailBean.getCreateTime())
                .setId(mmoEmailBean.getId()).setFromRoleId(mmoEmailBean.getFromRoleId()).setToRoleId(mmoEmailBean.getToRoleId())
                .setTitle(mmoEmailBean.getTitle()).setIsGet(mmoEmailBean.getGetFlag()).setHasArticle(mmoEmailBean.getHasArticle())
                .setMoney(mmoEmailBean.getMoney()==null?0:mmoEmailBean.getMoney()).setEquipmentId(mmoEmailBean.getEquipmentId()==null?-1:mmoEmailBean.getEquipmentId())
                .setIsGetMoney(mmoEmailBean.getGetMoneyFlag()).build();
        return emailDto;
    }

    public static EmailModel.EmailSimpleDto mmoEmailBeanToEmailSimpleDto(EmailBean mmoEmailBean) {
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
        bossBean.setBufferBeans(new CopyOnWriteArrayList<BaseBuffBean>());
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
        bossBean.setLevel(0);
        bossBean.setEquipmentLevel(0);
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
        bean.setArticleMessageId(medicineMessage.getId());
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
        bean.setArticleMessageId(equipmentMessage.getId());
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
                    .setLevel(m.getLevel())
                    .setEquipmentLevel(m.getEquipmentLevel())
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
        String json= JsonFormat.printToString(messageDataBuilder.build());
        if (sceneId!=null) {
            players = SceneBeanMessageCache.getInstance().get(sceneId).getRoles();
            for (Integer playerId:players){
                Channel c= ChannelMessageCache.getInstance().get(playerId);
                if (c!=null){
                    NotificationUtil.sendMessage(c,nettyResponse,json);
                }
            }

        }else{
            List<Role> roles = CopySceneProvider.getCopySceneBeanById(copySceneId).getRoles();
            for (Role role:roles) {
                if (role.getType().equals(RoleTypeCode.PLAYER.getCode())){
                    Channel c= ChannelMessageCache.getInstance().get(role.getId());
                    if (c!=null){
                        NotificationUtil.sendMessage(c,nettyResponse,json);
                    }
                }
            }
        }
    }

    /**
     * 转化为交易信息
     * @param firstArticles
     * @return
     */
    public static List<DealModel.ArticleDto> articlesToDealModelArticleDto(List<ArticleDto> firstArticles) {
        List<DealModel.ArticleDto> articleDtos=new ArrayList<>();
        if (firstArticles.size()>0) {
            for (ArticleDto firstArticle : firstArticles) {
                DealModel.ArticleDto articleDto=DealModel.ArticleDto.newBuilder()
                        .setDealArticleId(firstArticle.getDealArticleId()).setArticleType(firstArticle.getArticleType()).setArticleMessageId(firstArticle.getId())
                        .setEquipmentId(firstArticle.getEquipmentId()==null?-1:firstArticle.getEquipmentId()).setQuantity(firstArticle.getQuantity()).setNowDurability(firstArticle.getNowDurability()==null?-1:firstArticle.getNowDurability()).build();
                articleDtos.add(articleDto);
            }
        }
        return articleDtos;
    }

}

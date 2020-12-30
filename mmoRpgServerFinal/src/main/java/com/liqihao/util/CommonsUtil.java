package com.liqihao.util;


import com.liqihao.Cache.NpcMessageCache;
import com.liqihao.Cache.SkillMessageCache;
import com.liqihao.commons.ConstantValue;
import com.liqihao.commons.NettyResponse;
import com.liqihao.commons.StateCode;
import com.liqihao.commons.enums.CopySceneStatusCode;
import com.liqihao.commons.enums.RoleStatusCode;
import com.liqihao.commons.enums.RoleTypeCode;
import com.liqihao.dao.MmoBagPOJOMapper;
import com.liqihao.dao.MmoEquipmentBagPOJOMapper;
import com.liqihao.dao.MmoEquipmentPOJOMapper;
import com.liqihao.dao.MmoRolePOJOMapper;
import com.liqihao.pojo.MmoBagPOJO;
import com.liqihao.pojo.MmoEquipmentBagPOJO;
import com.liqihao.pojo.MmoEquipmentPOJO;
import com.liqihao.pojo.MmoRolePOJO;
import com.liqihao.pojo.baseMessage.*;
import com.liqihao.pojo.bean.*;
import com.liqihao.pojo.dto.ArticleDto;
import com.liqihao.pojo.dto.EquipmentDto;
import com.liqihao.protobufObject.CopySceneModel;
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
            for (BufferBean b:boss.getBufferBeans()) {
                CopySceneModel.BufferDto bufferDto= bufferBeanToBufferDto(b);
                bufferDtoList.add(bufferDto);
            }
        }
        bossDtoBuilder.addAllBufferDtos(bufferDtoList);
        return  bossDtoBuilder.build();
    }

    private static CopySceneModel.BufferDto bufferBeanToBufferDto(BufferBean b) {
        CopySceneModel.BufferDto.Builder bufferDtoBuilder=CopySceneModel.BufferDto.newBuilder();
        return bufferDtoBuilder.setId(b.getId()).setName(b.getName()).setFromRoleId(b.getFromRoleId())
                .setToRoleId(b.getToRoleId()).setCreateTime(b.getCreateTime()).setLastTime(b.getLastTime())
                .build();
    }

    public static CopySceneModel.RoleDto mmoSimpleRolesToCopyScneRoleDto(MmoSimpleRole role) {
        CopySceneModel.RoleDto.Builder roleDtoBuilder=CopySceneModel.RoleDto.newBuilder();
        List<CopySceneModel.BufferDto> bufferDtoList=new ArrayList<>();
        if (role.getBufferBeans().size()>0){
            for (BufferBean b:role.getBufferBeans()) {
                CopySceneModel.BufferDto bufferDto= bufferBeanToBufferDto(b);
                bufferDtoList.add(bufferDto);
            }
        }
        return roleDtoBuilder.setId(role.getId()).setBlood(role.getHp()).setNowBlood(role.getNowHp())
                .setNowMp(role.getNowMp()).addAllBufferDtos(bufferDtoList).setOnStatus(role.getOnStatus())
                .setTeamId(role.getTeamId()).setStatus(role.getStatus())
                .setName(role.getName()).setMp(role.getMp()).setType(role.getType()).build();

    }


    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        applicationContext = context;
        mmoBagPOJOMapper=(MmoBagPOJOMapper)context.getBean("mmoBagPOJOMapper");
        mmoEquipmentPOJOMapper=(MmoEquipmentPOJOMapper)context.getBean("mmoEquipmentPOJOMapper");
        equipmentBagPOJOMapper=(MmoEquipmentBagPOJOMapper)context.getBean("mmoEquipmentBagPOJOMapper");
        mmoRolePOJOMapper=(MmoRolePOJOMapper)context.getBean("mmoRolePOJOMapper");
    }
    private static MmoBagPOJOMapper mmoBagPOJOMapper;
    private static MmoEquipmentPOJOMapper mmoEquipmentPOJOMapper;
    private static MmoEquipmentBagPOJOMapper equipmentBagPOJOMapper;
    private static MmoRolePOJOMapper mmoRolePOJOMapper;
    public static CopySceneBean copySceneMessageToCopySceneBean(CopySceneMessage copySceneMessage) {
        CopySceneBean copySceneBean=new CopySceneBean();
        copySceneBean.setCreateTime(System.currentTimeMillis());
        copySceneBean.setEndTime(System.currentTimeMillis()+1000*copySceneMessage.getLastTime());
        copySceneBean.setMmoSimpleRoles(new CopyOnWriteArrayList<>());
        copySceneBean.setStatus(CopySceneStatusCode.ONDOING.getCode());
        copySceneBean.setBossIds(copySceneMessage.getBossIds());
        copySceneBean.setId(copySceneMessage.getId());
        copySceneBean.setLastTime(copySceneMessage.getLastTime());
        copySceneBean.setName(copySceneMessage.getName());
        return copySceneBean;
    }

    public static BossBean bossMessageToBossBean(BossMessage bossMessage) {
        BossBean bossBean=new BossBean();
        bossBean.setBufferBeans(new CopyOnWriteArrayList<BufferBean>());
        bossBean.setCdMap(new HashMap<>());
        bossBean.setHatredMap(new ConcurrentHashMap<>());
        bossBean.setHp(bossMessage.getBlood());
        bossBean.setNowMp(bossMessage.getMp());
        bossBean.setStatus(RoleStatusCode.ALIVE.getCode());
        bossBean.setType(RoleTypeCode.ENEMY.getCode());
        bossBean.setAttack(bossMessage.getAttack());
        bossBean.setNowHp(bossMessage.getBlood());
        bossBean.setDamageAdd(bossMessage.getDamageAdd());
        bossBean.setId(bossMessage.getId());
        bossBean.setEquipmentIds(bossMessage.getEquipmentIds());
        bossBean.setMedicines(bossMessage.getMedicines());
        bossBean.setMoney(bossMessage.getMoney());
        bossBean.setMp(bossMessage.getMp());
        bossBean.setStatus(RoleStatusCode.ALIVE.getCode());
        bossBean.setName(bossMessage.getName());
        bossBean.setSkillIds(bossMessage.getSkillIds());
        List<SkillBean> skillBeans=skillIdsToSkillBeans(bossMessage.getSkillIds());
        bossBean.setSkillBeans(skillBeans);
        return bossBean;
    }
    /**
     * 根据channle获取线程池线程下表
     */
    public static int getIndexByChannel(Channel channel) {
        int threadSize = LogicThreadPool.getInstance().getThreadSize();
        Integer index = channel.hashCode() & (threadSize - 1);
        return index;
    }
    /**
     * 判断是否登陆
     * @param channel
     * @return
     */
    public static MmoSimpleRole checkLogin(Channel channel){
        MmoSimpleRole mmoSimpleRole= CommonsUtil.getRoleByChannel(channel);
        if (mmoSimpleRole==null){
            NettyResponse errotResponse=new NettyResponse(StateCode.FAIL, ConstantValue.FAIL_RESPONSE,"请先登录".getBytes());
            channel.writeAndFlush(errotResponse);
            return null;
        }
        return mmoSimpleRole;
    }

    /**
     * 人物信息入库
     */
    public static  void RoleInfoIntoDataBase(MmoSimpleRole mmoSimpleRole){
        MmoRolePOJO mmoRolePOJO=new MmoRolePOJO();
        mmoRolePOJO.setId(mmoSimpleRole.getId());
        mmoRolePOJO.setStatus(mmoSimpleRole.getStatus());
        mmoRolePOJO.setOnstatus(mmoSimpleRole.getOnStatus());






        mmoRolePOJO.setMmosceneid(mmoSimpleRole.getMmosceneid());
        mmoRolePOJO.setName(mmoSimpleRole.getName());
        mmoRolePOJO.setSkillIds(CommonsUtil.listToString(mmoSimpleRole.getSkillIdList()));
        mmoRolePOJO.setType(mmoSimpleRole.getType());
        mmoRolePOJOMapper.updateByPrimaryKeySelective(mmoRolePOJO);
    }
    /**
     * 背包入库
     * @param backPackManager
     * @param roleId
     */
    public static  void bagIntoDataBase(BackPackManager backPackManager,Integer roleId){
        List<ArticleDto> articles=backPackManager.getBackpacks();
        //需要修改或者新增的记录
        for (ArticleDto a:articles) {
            MmoBagPOJO mmoBagPOJO=new MmoBagPOJO();
            mmoBagPOJO.setArticletype(a.getArticleType());
            mmoBagPOJO.setNumber(a.getQuantity());
            mmoBagPOJO.setRoleId(roleId);
            mmoBagPOJO.setwId(a.getId());
            if (a.getBagId()!=null){
                mmoBagPOJO.setBagId(a.getBagId());
                mmoBagPOJOMapper.updateByPrimaryKey(mmoBagPOJO);
            }else{
                //新的
                mmoBagPOJOMapper.insert(mmoBagPOJO);
            }
        }
        //需要删除的记录
        List<Integer> bagIds=backPackManager.getNeedDeleteBagId();
        for (Integer id:bagIds){
            mmoBagPOJOMapper.deleteByPrimaryKey(id);
        }
    }

    /**
     * 装备入库
     * @param mmoSimpleRole
     */
    public static void equipmentIntoDataBase(MmoSimpleRole mmoSimpleRole){
        List<EquipmentDto> dtos=mmoSimpleRole.getEquipments();
        for (EquipmentDto e:dtos) {
            MmoEquipmentBagPOJO equipmentBagPOJO = new MmoEquipmentBagPOJO();
            //装备栏
            equipmentBagPOJO.setEquipmentId(e.getEquipmentId());
            equipmentBagPOJO.setRoleid(mmoSimpleRole.getId());
            if (e.getEquipmentBagId()!=null) {
                //主键
                equipmentBagPOJO.setEquipmentbagId(e.getEquipmentBagId());
                equipmentBagPOJOMapper.updateByPrimaryKey(equipmentBagPOJO);
            }else{
                equipmentBagPOJOMapper.insert(equipmentBagPOJO);
            }
            //装备入库
            MmoEquipmentPOJO equipmentPOJO=new MmoEquipmentPOJO();
            equipmentPOJO.setId(e.getEquipmentId());
            equipmentPOJO.setMessageId(e.getId());
            equipmentPOJO.setNowdurability(e.getNowdurability());
            mmoEquipmentPOJOMapper.updateByPrimaryKey(equipmentPOJO);
        }
        //需要删除的记录
        List<Integer> equBagIds=mmoSimpleRole.getNeedDeleteEquipmentIds();
        for (Integer id:equBagIds){
            equipmentBagPOJOMapper.deleteByPrimaryKey(id);
        }
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
        bean.setArticleId(null);
        bean.setCd(medicineMessage.getCd());
        bean.setLastTime(medicineMessage.getLastTime());
        bean.setMedicineType(medicineMessage.getMedicineType());
        bean.setSecondValue(medicineMessage.getSecondValue());
        bean.setArticleType(medicineMessage.getArticleType());
        bean.setDescription(medicineMessage.getDescription());
        bean.setId(medicineMessage.getId());
        bean.setName(medicineMessage.getName());
        bean.setSingleFlag(medicineMessage.getSingleFlag());
        bean.setDamageType(medicineMessage.getDamageType());
        bean.setDamageValue(medicineMessage.getDamageValue());
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
        bean.setAttackAdd(equipmentMessage.getAttackAdd());
        bean.setNowDurability(equipmentMessage.getDurability());
        bean.setArticleType(equipmentMessage.getArticleType());
        bean.setDamageAdd(equipmentMessage.getDamageAdd());
        bean.setDescription(equipmentMessage.getDescription());
        bean.setQuantity(1);
        bean.setDurability(equipmentMessage.getDurability());
        bean.setId(equipmentMessage.getId());
        bean.setName(equipmentMessage.getName());
        bean.setPosition(equipmentMessage.getPosition());
        bean.setSingleFlag(equipmentMessage.getSingleFlag());
        return bean;
    }

    public static MmoSimpleRole NpcToMmoSimpleRole(MmoSimpleNPC npc) {
        MmoSimpleRole roleTemp = new MmoSimpleRole();
        roleTemp.setId(npc.getId());
        roleTemp.setName(npc.getName());
        roleTemp.setMmosceneid(npc.getMmosceneid());
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

    public static void main(String[] args) {
//        List<Integer> list=new ArrayList<>();
//        list.add(1);
//        list.add(222);
//        System.out.println(listToString(list));
//        List<Integer> list2=new ArrayList<>();
//        System.out.println(listToString(list2)+"-----");
//        List<Integer> list3=new ArrayList<>();
//        list3.add(1);
//        System.out.println(listToString(list3));
        String str="祝贺吧。-穿越时空，能知过去未来，收集所有蒙面超人的力量。-他就是蒙面超人时王!";
        String str2="";
        String str3="我系时王zio";
        System.out.println(splitToStringList(str).get(0));
        System.out.println(splitToStringList(str).get(1));
        System.out.println(splitToStringList(str).get(2));
        System.out.println(splitToStringList(str2));
        System.out.println(splitToStringList(str3));
    }

    public static SceneBean sceneMessageToSceneBean(SceneMessage m) {
        SceneBean sceneBean=new SceneBean();
        sceneBean.setId(m.getId());
        sceneBean.setName(m.getPlaceName());
        sceneBean.setCanScenes(split(m.getCanScene()));
        sceneBean.setRoles(new ArrayList<>());
        List<Integer> npcs=new ArrayList<>();
        for (MmoSimpleNPC mpc: NpcMessageCache.getInstance().values()) {
            if (mpc.getMmosceneid().equals(m.getId())){
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
    public static List<SkillBean> skillIdsToSkillBeans(String skillIds) {
        List<SkillBean> list=new ArrayList<>();
        for (Integer id:split(skillIds)) {
            SkillMessage message= SkillMessageCache.getInstance().get(id);
            SkillBean skillBean=new SkillBean();
            skillBean.setId(message.getId());
            skillBean.setSkillType(message.getSkillType());
            skillBean.setAddPercon(message.getAddPercon());
            skillBean.setConsumeType(message.getConsumeType());
            skillBean.setSkillName(message.getSkillName());
            skillBean.setBaseDamage(message.getBaseDamage());
            skillBean.setConsumeNum(message.getConsumeNum());
            skillBean.setCd(message.getCd());
            skillBean.setBufferIds(split(message.getBufferIds()));
            list.add(skillBean);
        }
        return list;
    }


}

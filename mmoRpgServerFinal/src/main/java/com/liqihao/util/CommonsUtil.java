package com.liqihao.util;


import com.liqihao.Cache.MmoCache;
import com.liqihao.commons.enums.ArticleTypeCode;
import com.liqihao.dao.MmoBagPOJOMapper;
import com.liqihao.dao.MmoEquipmentBagPOJOMapper;
import com.liqihao.dao.MmoEquipmentPOJOMapper;
import com.liqihao.pojo.MmoBagPOJO;
import com.liqihao.pojo.MmoEquipmentBagPOJO;
import com.liqihao.pojo.MmoEquipmentPOJO;
import com.liqihao.pojo.baseMessage.EquipmentMessage;
import com.liqihao.pojo.baseMessage.MedicineMessage;
import com.liqihao.pojo.baseMessage.SceneMessage;
import com.liqihao.pojo.baseMessage.SkillMessage;
import com.liqihao.pojo.bean.*;
import com.liqihao.pojo.dto.ArticleDto;
import com.liqihao.pojo.dto.EquipmentDto;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 字符串处理类
 * @author lqhao
 */
@Component
public class CommonsUtil implements ApplicationContextAware {


    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        applicationContext = context;
        mmoBagPOJOMapper=(MmoBagPOJOMapper)context.getBean("mmoBagPOJOMapper");
        mmoEquipmentPOJOMapper=(MmoEquipmentPOJOMapper)context.getBean("mmoEquipmentPOJOMapper");
        equipmentBagPOJOMapper=(MmoEquipmentBagPOJOMapper)context.getBean("mmoEquipmentBagPOJOMapper");
    }
    private static MmoBagPOJOMapper mmoBagPOJOMapper;
    private static MmoEquipmentPOJOMapper mmoEquipmentPOJOMapper;
    private static MmoEquipmentBagPOJOMapper equipmentBagPOJOMapper;
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
//            MmoEquipmentPOJO equipmentPOJO=new MmoEquipmentPOJO();
        }
        //需要删除的记录
        List<Integer> equBagIds=mmoSimpleRole.getNeedDeleteEquipmentIds();
        for (Integer id:equBagIds){
            equipmentBagPOJOMapper.deleteByPrimaryKey(id);
        }
    }


    public static Integer getRoleIdByChannel(Channel channel){
        AttributeKey<Integer> key = AttributeKey.valueOf("roleId");
        if (channel.hasAttr(key) && channel.attr(key).get() != null)
        {
            return channel.attr(key).get();
        }
        return null;
    }
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
        roleTemp.setOnstatus(npc.getOnstatus());
        roleTemp.setBlood(npc.getBlood());
        roleTemp.setNowBlood(npc.getNowBlood());
        roleTemp.setMp(npc.getNowBlood());
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
        for (MmoSimpleNPC mpc:MmoCache.getInstance().getNpcMessageConcurrentHashMap().values()) {
            if (mpc.getMmosceneid().equals(m.getId())){
                npcs.add(mpc.getId());
            }
        }
        sceneBean.setNpcs(npcs);
        return sceneBean;
    }

    public static List<SkillBean> skillIdsToSkillBeans(String skillIds) {
        List<SkillBean> list=new ArrayList<>();
        for (Integer id:split(skillIds)) {
            SkillMessage message=MmoCache.getInstance().getSkillMessageConcurrentHashMap().get(id);
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

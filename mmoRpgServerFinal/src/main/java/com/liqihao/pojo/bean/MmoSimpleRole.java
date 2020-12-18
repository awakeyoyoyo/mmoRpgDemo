package com.liqihao.pojo.bean;


import com.liqihao.commons.enums.*;
import com.liqihao.pojo.MmoRolePOJO;
import com.liqihao.pojo.dto.EquipmentDto;
import com.liqihao.protobufObject.PlayModel;
import com.liqihao.util.ScheduledThreadPoolUtil;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * 缓存中存储的人物类
 */
public class MmoSimpleRole extends MmoRolePOJO {
    private Integer Blood;
    private Integer nowBlood;
    private Integer mp;
    private Integer nowMp;
    private HashMap<Integer,Long> cdMap;
    private List<Integer> skillIdList;
    private List<SkillBean> skillBeans;
    private CopyOnWriteArrayList<BufferBean> bufferBeans;
    private Integer attack;
    private BackPackManager backpackManager;
    private List<Integer> needDeleteEquipmentIds=new ArrayList<>();
    private double damageAdd;

    public double getDamageAdd() {
        return damageAdd;
    }

    public void setDamageAdd(double damageAdd) {
        this.damageAdd = damageAdd;
    }
    public List<Integer> getNeedDeleteEquipmentIds() {
        return needDeleteEquipmentIds;
    }

    public void setNeedDeleteEquipmentIds(List<Integer> needDeleteEquipmentIds) {
        this.needDeleteEquipmentIds = needDeleteEquipmentIds;
    }

    //装备栏
    private HashMap<Integer,EquipmentBean> equipmentBeanHashMap;

    public HashMap<Integer, EquipmentBean> getEquipmentBeanHashMap() {
        return equipmentBeanHashMap;
    }

    public void setEquipmentBeanHashMap(HashMap<Integer, EquipmentBean> equipmentBeanHashMap) {
        this.equipmentBeanHashMap = equipmentBeanHashMap;
    }

    public BackPackManager getBackpackManager() {
        return backpackManager;
    }

    public void setBackpackManager(BackPackManager backpackManager) {
        this.backpackManager = backpackManager;
    }

    public List<SkillBean> getSkillBeans() {
        return skillBeans;
    }

    public void setSkillBeans(List<SkillBean> skillBeans) {
        this.skillBeans = skillBeans;
    }



    public Integer getAttack() {
        return attack;
    }

    public void setAttack(Integer attack) {
        this.attack = attack;
    }
    public CopyOnWriteArrayList<BufferBean> getBufferBeans() {
        return bufferBeans;
    }

    public void setBufferBeans(CopyOnWriteArrayList<BufferBean> bufferBeans) {
        this.bufferBeans = bufferBeans;
    }

    public Integer getNowMp() {
        return nowMp;
    }

    public void setNowMp(Integer nowMp) {
        this.nowMp = nowMp;
    }

    public List<Integer> getSkillIdList() {
        return skillIdList;
    }

    public void setSkillIdList(List<Integer> skillIdList) {
        this.skillIdList = skillIdList;
    }

    public Integer getMp() {
        return mp;
    }

    public void setMp(Integer mp) {
        this.mp = mp;
    }

    public Integer getBlood() {
        return Blood;
    }

    public void setBlood(Integer blood) {
        Blood = blood;
    }

    public Integer getNowBlood() {
        return nowBlood;
    }

    public void setNowBlood(Integer nowBlood) {
        this.nowBlood = nowBlood;
    }



    public HashMap<Integer, Long> getCdMap() {
        return cdMap;
    }

    public void setCdMap(HashMap<Integer, Long> cdMap) {
        this.cdMap = cdMap;
    }
    //使用道具
    public Boolean useArticle(Integer articleId) {
        Article article=backpackManager.getArticleByArticleId(articleId);
        if (article!=null&&article.getArticleTypeCode().equals(ArticleTypeCode.MEDICINE.getCode())){
            //药品
            MedicineBean medicineBean=(MedicineBean) article;
            //删减
            backpackManager.useOrAbandanArticle(articleId,1);
            Boolean flag=medicineBean.useMedicene(getId());
            return flag;
        }else if (article!=null&&article.getArticleTypeCode().equals(ArticleTypeCode.EQUIPMENT.getCode())){
            //装备
            EquipmentBean equipmentBean=(EquipmentBean) article;
            //删减
            backpackManager.useOrAbandanArticle(articleId,1);
            //穿
            return useEquipment(equipmentBean);
        }else{
            return false;
        }

    }
    //穿装备 or替换装备
    private Boolean useEquipment(EquipmentBean equipmentBean){
        //判断该位置是否有装备
        EquipmentBean oldBean =getEquipmentBeanHashMap().get(equipmentBean.getPosition());
        if (oldBean!=null){
            //放回背包内
            //背包新增数据
            //修改人物属性
            setAttack(getAttack()-oldBean.getAttackAdd());
            setDamageAdd(getDamageAdd()-oldBean.getDamageAdd());
            backpackManager.put(oldBean);
        }
        //背包减少装备
        backpackManager.useOrAbandanArticle(equipmentBean.getArticleId(),1);
        //装备栏增加装备
        equipmentBeanHashMap.put(equipmentBean.getPosition(),equipmentBean);
        //人物属性
        setAttack(getAttack()+equipmentBean.getAttackAdd());
        setDamageAdd(getDamageAdd()+equipmentBean.getDamageAdd());
        return true;
    }
    //脱装备
    public Boolean unUseEquipment(Integer position){
        //判断该位置是否有装备
        EquipmentBean equipmentBean =getEquipmentBeanHashMap().get(position);
        if (equipmentBean==null){
            //无装备
            return false;
        }else{
            equipmentBeanHashMap.remove(position);
            //装备栏数据库减少该装备
            if (equipmentBean.getEquipmentBagId()!=null) {
                needDeleteEquipmentIds.add(equipmentBean.getEquipmentBagId());
            }
            //装备栏id为null
            equipmentBean.setEquipmentBagId(null);
            //放入背包
            backpackManager.put(equipmentBean);
            setAttack(getAttack()-equipmentBean.getAttackAdd());
            setDamageAdd(getDamageAdd()-equipmentBean.getDamageAdd());
            return  true;
        }
    }
    //获取装备栏所有信息
    public List<EquipmentDto> getEquipments(){
        List<EquipmentDto> equipmentDtos=new ArrayList<>();
        for (EquipmentBean bean:equipmentBeanHashMap.values()) {
            EquipmentDto equipmentDto=new EquipmentDto();
            equipmentDto.setId(bean.getId());
            equipmentDto.setNowdurability(bean.getNowDurability());
            equipmentDto.setPosition(bean.getPosition());
            equipmentDto.setEquipmentBagId(bean.getEquipmentBagId());
            equipmentDto.setEquipmentId(bean.getEquipmentId());
            equipmentDtos.add(equipmentDto);
        }
        return equipmentDtos;
    }
    //根据skillI获取技能
    public  SkillBean getSkillBeanBySkillId(Integer skillId){
        for (SkillBean b:getSkillBeans()) {
            if (b.getId().equals(skillId)){ return b;}
        }
        return null;
    }
    public List<PlayModel.RoleIdDamage> useSkill(List<MmoSimpleNPC> target, Integer skillId) {
        SkillBean skillBean= getSkillBeanBySkillId(skillId);
        //武器耐久度-2
        EquipmentBean equipmentBean=this.getEquipmentBeanHashMap().get(6);
        if (equipmentBean!=null){
            equipmentBean.setNowDurability(equipmentBean.getNowDurability()-2);
            if (equipmentBean.getNowDurability()<0){
                equipmentBean.setNowDurability(0);
            }
        }
        if (skillBean.getConsumeType().equals(ConsuMeTypeCode.HP.getCode())){
            //扣血
            setNowBlood(getNowBlood()-skillBean.getConsumeNum());
        }else {
            //扣篮
            setNowMp(getNowMp()-skillBean.getConsumeNum());
            //判断是否已经有自动回蓝任务
            ConcurrentHashMap<String, ScheduledFuture<?>> replyMpRoleMap=ScheduledThreadPoolUtil.getReplyMpRole();
            //自动回蓝任务的key
            String key=getId()+"AUTOMP";
            if (!replyMpRoleMap.containsKey(key)) {
                //number为空 代表着自动回蓝
                ScheduledThreadPoolUtil.ReplyMpTask replyMpTask = new ScheduledThreadPoolUtil.ReplyMpTask(getId(), null,DamageTypeCode.MP.getCode(),key);
                // 周期性执行，每5秒执行一次
                ScheduledFuture<?> t=ScheduledThreadPoolUtil.getScheduledExecutorService().scheduleAtFixedRate(replyMpTask, 0, 5, TimeUnit.SECONDS);
                replyMpRoleMap.put(key,t);
            }
        }
        List<PlayModel.RoleIdDamage> list=new ArrayList<>();
        // 生成一个角色扣血或者扣篮
        PlayModel.RoleIdDamage.Builder damageU=PlayModel.RoleIdDamage.newBuilder();
        damageU.setFromRoleId(getId());
        damageU.setToRoleId(getId());
        damageU.setAttackStyle(AttackStyleCode.USESKILL.getCode());
        damageU.setBufferId(-1);
        damageU.setDamage(skillBean.getConsumeNum());
        damageU.setDamageType(skillBean.getConsumeType());
        damageU.setMp(getNowMp());
        damageU.setNowblood(getNowBlood());
        damageU.setSkillId(skillBean.getId());
        damageU.setState(getStatus());
        list.add(damageU.build());
        //攻击怪物

        for (MmoSimpleNPC mmoSimpleNPC:target){
            Integer hp=mmoSimpleNPC.getNowBlood();
            Integer reduce=0;
            if (skillBean.getSkillType().equals(SkillTypeCode.FIED.getCode())){
                //固伤 只有技能伤害
                reduce=(int)Math.ceil(skillBean.getBaseDamage()*(1+this.getDamageAdd()));
                hp-=reduce;
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
            damageR.setFromRoleId(getId());
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
                mmoSimpleNPC.npcAttack(getId());
            }
        }
        //cd
        Map<Integer,Long> map=getCdMap();
        Long time=System.currentTimeMillis();
        int addTime=skillBean.getCd()*1000;
        map.put(skillBean.getId(),time+addTime);
        //buffer
        skillBean.useBuffer(target,getId());
        return  list;
    }

}

package com.liqihao.pojo.bean;

import com.liqihao.Cache.MmoCache;
import com.liqihao.commons.ConstantValue;
import com.liqihao.commons.NettyResponse;
import com.liqihao.commons.enums.*;
import com.liqihao.pojo.MmoRolePOJO;
import com.liqihao.protobufObject.PlayModel;
import com.liqihao.util.ScheduledThreadPoolUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

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
            //todo 装备
            return false;
        }else{
            return false;
        }

    }


    public List<PlayModel.RoleIdDamage> useSkill(List<MmoSimpleNPC> target, Integer skillId) {
        SkillBean skillBean= getSkillBeans().get(skillId);
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

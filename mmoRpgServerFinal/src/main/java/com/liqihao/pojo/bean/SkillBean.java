package com.liqihao.pojo.bean;

import com.liqihao.commons.enums.BufferStyleCode;
import com.liqihao.commons.enums.BufferTypeCode;
import com.liqihao.pojo.baseMessage.BufferMessage;
import com.liqihao.pojo.bean.bufferBean.AttractBufferBean;
import com.liqihao.pojo.bean.bufferBean.BaseBufferBean;
import com.liqihao.pojo.bean.bufferBean.HpBufferBean;
import com.liqihao.pojo.bean.bufferBean.MpBufferBean;
import com.liqihao.pojo.bean.roleBean.Role;
import com.liqihao.util.ScheduledThreadPoolUtil;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * 技能bean
 * @author lqhao
 */
public class SkillBean {
    /**
     * 基本信息id
     */
    private Integer id;
    /**
     * 技能名称
     */
    private String  skillName;
    /**
     * 基础伤害
     */
    private Integer baseDamage;
    /**
     * cd时间
     */
    private Integer cd;
    /**
     * 消耗类型
     */
    private Integer consumeType;
    /**
     * 消耗
     */
    private Integer consumeNum;
    /**
     * 所造成buffer
     */
    private List<Integer> bufferIds;
    /**
     * 技能类型
     */
    private Integer skillType;
    /**
     * 按照攻击力加成
     */
    private double addPerson;
    /**
     * 技能攻击类型
     */
    private Integer skillAttackType;
    /**
     * 吟唱时间
     */
    private Integer chantTime;
    /**
     * 伤害类型
     */
    private Integer skillDamageType;

    public Integer getChantTime() {
        return chantTime;
    }

    public void setChantTime(Integer chantTime) {
        this.chantTime = chantTime;
    }

    public Integer getSkillDamageType() {
        return skillDamageType;
    }

    public void setSkillDamageType(Integer skillDamageType) {
        this.skillDamageType = skillDamageType;
    }

    public Integer getSkillAttackType() {
        return skillAttackType;
    }

    public void setSkillAttackType(Integer skillAttackType) {
        this.skillAttackType = skillAttackType;
    }

    /**
     * 作用于npc
     */
    public BaseBufferBean bufferToPeople(BufferMessage bufferMessage, Role fromRole, Role toRole){
        //生成buffer类
        BaseBufferBean bufferBean;
        if (bufferMessage.getBufferStyle().equals(BufferTypeCode.GG_ATTACK.getCode())){
            bufferBean=new AttractBufferBean();
        }else if (bufferMessage.getBufferStyle().equals(BufferTypeCode.REDUCE_MP.getCode())){
            bufferBean=new MpBufferBean(-1);
        }else{
            bufferBean=new HpBufferBean(-1);
        }
        bufferBean.setFromRoleType(fromRole.getType());
        bufferBean.setToRoleType(toRole.getType());
        bufferBean.setFromRoleId(fromRole.getId());
        bufferBean.setCreateTime(System.currentTimeMillis());
        bufferBean.setBufferMessageId(bufferMessage.getId());
        bufferBean.setToRoleId(toRole.getId());
//        bufferBeans.add(bufferBean);
        bufferBean.setToRoleId(toRole.getId());
        //人物增加buffer
        toRole.getBufferBeans().add(bufferBean);
        //线程池中放入任务
        if (bufferMessage.getBufferStyle().equals(BufferStyleCode.SPACE_DO.getCode())) {
            //间隔生效
            Integer count=bufferMessage.getLastTime()/bufferMessage.getSpaceTime();
            ScheduledThreadPoolUtil.BufferTask bufferTask = new ScheduledThreadPoolUtil.BufferTask(bufferBean, count, toRole);
            //查看是否已经有了该buffer 有则覆盖无则直接加入
            String key = toRole.getId().toString() + bufferBean.getBufferMessageId().toString()+toRole.getName()+fromRole.getName();
            ConcurrentHashMap<String, ScheduledFuture<?>> bufferRole = ScheduledThreadPoolUtil.getBufferRole();
            if (bufferRole.containsKey(key)) {
                bufferRole.get(key).cancel(false);
            }
            ScheduledFuture<?> t = ScheduledThreadPoolUtil.getScheduledExecutorService().scheduleAtFixedRate(bufferTask, 0, bufferMessage.getSpaceTime(), TimeUnit.SECONDS);
            bufferRole.put(key, t);
        }else{
            //持续生效
            ScheduledThreadPoolUtil.BufferTask bufferTask = new ScheduledThreadPoolUtil.BufferTask(bufferBean, 1, toRole);
            //用该嘲讽buffer id 作为主键，目的是让其每次都会覆盖就得嘲讽buffer
            String key = toRole.getId().toString() + bufferMessage.getId().toString()+toRole.getName()+fromRole.getName();
            ConcurrentHashMap<String, ScheduledFuture<?>> bufferRole = ScheduledThreadPoolUtil.getBufferRole();
            if (bufferRole.containsKey(key)) {
                bufferRole.get(key).cancel(false);
            }
            ScheduledFuture<?> t = ScheduledThreadPoolUtil.getScheduledExecutorService().scheduleAtFixedRate(bufferTask, 0, bufferMessage.getLastTime(), TimeUnit.SECONDS);
            bufferRole.put(key, t);
        }
        return bufferBean;
    }
    /**
     * 计算伤害
     * @param
     * @return
     */
    private Integer calculateDamage(){

        return 0;
    }

    public double getAddPerson() {
        return addPerson;
    }

    public void setAddPerson(double addPerson) {
        this.addPerson = addPerson;
    }
    public Integer getSkillType() {
        return skillType;
    }

    public void setSkillType(Integer skillType) {
        this.skillType = skillType;
    }
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public Integer getBaseDamage() {
        return baseDamage;
    }

    public void setBaseDamage(Integer baseDamage) {
        this.baseDamage = baseDamage;
    }

    public Integer getCd() {
        return cd;
    }

    public void setCd(Integer cd) {
        this.cd = cd;
    }

    public Integer getConsumeType() {
        return consumeType;
    }

    public void setConsumeType(Integer consumeType) {
        this.consumeType = consumeType;
    }

    public Integer getConsumeNum() {
        return consumeNum;
    }

    public void setConsumeNum(Integer consumeNum) {
        this.consumeNum = consumeNum;
    }

    public List<Integer> getBufferIds() {
        return bufferIds;
    }

    public void setBufferIds(List<Integer> bufferIds) {
        this.bufferIds = bufferIds;
    }

}

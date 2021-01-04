package com.liqihao.pojo.bean;

import com.liqihao.pojo.baseMessage.BufferMessage;
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
    private Integer id;
    private String  skillName;
    private Integer baseDamage;
    private Integer cd;
    private Integer consumeType;
    private Integer consumeNum;
    private List<Integer> bufferIds;
    private Integer skillType;
    private double addPerson;
    private Integer skillAttackType;

    public Integer getSkillAttackType() {
        return skillAttackType;
    }

    public void setSkillAttackType(Integer skillAttackType) {
        this.skillAttackType = skillAttackType;
    }

    /**
     * 作用于npc
     */
    public BufferBean bufferToPeople(BufferMessage b,Role fromRole,Role toRole){
        //生成buffer类
        BufferBean bufferBean=new BufferBean();
        bufferBean.setFromRoleType(fromRole.getType());
        bufferBean.setToRoleType(toRole.getType());
        bufferBean.setFromRoleId(fromRole.getId());
        bufferBean.setBuffNum(b.getBuffNum());
        bufferBean.setBuffType(b.getBuffType());
        bufferBean.setName(b.getName());
        bufferBean.setId(b.getId());
        bufferBean.setLastTime(b.getLastTime());
        bufferBean.setSpaceTime(b.getSpaceTime());
        bufferBean.setCreateTime(System.currentTimeMillis());
//        bufferBeans.add(bufferBean);
        bufferBean.setToRoleId(toRole.getId());
        Integer count=bufferBean.getLastTime()/bufferBean.getSpaceTime();
        //增加bufferid
        toRole.getBufferBeans().add(bufferBean);
        //线程池中放入任务
        ScheduledThreadPoolUtil.BufferTask bufferTask = new ScheduledThreadPoolUtil.BufferTask(bufferBean, count,toRole);
        //查看是否已经有了该buffer 有则覆盖无则直接加入
        Integer key=Integer.parseInt(toRole.getId().toString()+bufferBean.getId().toString());
        ConcurrentHashMap<Integer, ScheduledFuture<?>> bufferRole=ScheduledThreadPoolUtil.getBufferRole();
        if (bufferRole.containsKey(key)){
            bufferRole.get(key).cancel(false);
        }
        ScheduledFuture<?> t =ScheduledThreadPoolUtil.getScheduledExecutorService().scheduleAtFixedRate(bufferTask,0,bufferBean.getSpaceTime(), TimeUnit.SECONDS);
        bufferRole.put(key,t);
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

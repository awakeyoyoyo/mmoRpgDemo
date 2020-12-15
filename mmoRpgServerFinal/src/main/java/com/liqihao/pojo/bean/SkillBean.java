package com.liqihao.pojo.bean;

import com.liqihao.Cache.MmoCache;
import com.liqihao.pojo.baseMessage.BufferMessage;
import com.liqihao.util.ScheduledThreadPoolUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class SkillBean {
    private Integer id;
    private String  skillName;
    private Integer baseDamage;
    private Integer cd;
    private Integer consumeType;
    private Integer consumeNum;
    private List<Integer> bufferIds;
    private Integer skillType;
    private double addPercon;


    public void useBuffer(List<MmoSimpleNPC> target,Integer roleId){
        //查看是否有buffer
        List<Integer> buffers=getBufferIds();
        List<BufferBean> bufferBeans=new ArrayList<>();
        for (Integer buffId:buffers) {
            BufferMessage b= MmoCache.getInstance().getBufferMessageConcurrentHashMap().get(buffId);
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
                ConcurrentHashMap<Integer, ScheduledFuture<?>> bufferRole=ScheduledThreadPoolUtil.getBufferRole();
                if (bufferRole.containsKey(key)){
                    bufferRole.get(key).cancel(false);
                }
                ScheduledFuture<?> t =ScheduledThreadPoolUtil.getScheduledExecutorService().scheduleAtFixedRate(bufferTask,0,bufferBean.getSpaceTime(), TimeUnit.SECONDS);
                bufferRole.put(key,t);
            }
        }
    }


    public double getAddPercon() {
        return addPercon;
    }

    public void setAddPercon(double addPercon) {
        this.addPercon = addPercon;
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

package com.liqihao.pojo.bean;

import com.liqihao.commons.enums.RoleStatusCode;
import com.liqihao.pojo.baseMessage.NPCMessage;
import com.liqihao.util.ScheduledThreadPoolUtil;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;


/**
 * npc
 * @author lqhao
 */
public class MmoSimpleNPC extends NPCMessage {
    private volatile Integer nowBlood;
    private volatile Integer nowMp;
    private CopyOnWriteArrayList<BufferBean> bufferBeans;
    private SceneBean nowScene;
    public final ReentrantReadWriteLock hpRwLock = new ReentrantReadWriteLock();
    public final ReentrantReadWriteLock mpRwLock = new ReentrantReadWriteLock();
    public SceneBean getNowScene() {
        return nowScene;
    }

    public void setNowScene(SceneBean nowScene) {
        this.nowScene = nowScene;
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

    /**
     *     判断是否死亡,并且更改状态
     */
    public void checkDie(){
        if (nowBlood<=0){
            super.setStatus(RoleStatusCode.DIE.getCode());
        }
    }

    public Integer getNowBlood() {
        return nowBlood;
    }
    public void reduceNowBlood(Integer num) {
        this.nowBlood +=num;
    }
    public void addNowBlood(Integer num) {
        this.nowBlood -=num;
    }
    public void setNowBlood(Integer nowBlood) {
        this.nowBlood = nowBlood;
    }

    public void npcAttack(Integer roleId){
        ScheduledFuture<?> t= ScheduledThreadPoolUtil.getNpcTaskMap().get(getId());
        if (t!=null){
            //代表着该npc正在攻击一个目标
            return;
        }else{
            ScheduledThreadPoolUtil.NpcAttackTask npcAttackTask=new ScheduledThreadPoolUtil.NpcAttackTask(roleId,getId());
            t=ScheduledThreadPoolUtil.getScheduledExecutorService().scheduleAtFixedRate(npcAttackTask,0,6, TimeUnit.SECONDS);
            ScheduledThreadPoolUtil.getNpcTaskMap().put(getId(),t);
        }
    }

}

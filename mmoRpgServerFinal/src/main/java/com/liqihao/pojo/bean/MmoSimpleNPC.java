package com.liqihao.pojo.bean;

import com.liqihao.commons.enums.RoleStatusCode;
import com.liqihao.pojo.baseMessage.NPCMessage;

import java.util.concurrent.CopyOnWriteArrayList;


public class MmoSimpleNPC extends NPCMessage {
    private Integer nowBlood;
    private Integer nowMp;
    private CopyOnWriteArrayList<BufferBean> bufferBeans;
    private SceneBean nowScene;

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

    //判断是否死亡,并且更改状态
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
}

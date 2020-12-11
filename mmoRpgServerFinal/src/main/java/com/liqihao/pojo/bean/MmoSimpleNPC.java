package com.liqihao.pojo.bean;

import com.liqihao.commons.enums.RoleStatusCode;
import com.liqihao.pojo.baseMessage.NPCMessage;


public class MmoSimpleNPC extends NPCMessage {
    private Integer nowBlood;
    private Integer nowMp;
    private BufferManager bufferManager;

    public BufferManager getBufferManager() {
        return bufferManager;
    }

    public void setBufferManager(BufferManager bufferManager) {
        this.bufferManager = bufferManager;
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

package com.liqihao.pojo.bean;

import com.liqihao.commons.ConstantValue;
import com.liqihao.commons.RoleStatusCode;
import com.liqihao.pojo.baseMessage.NPCMessage;

public class MmoSimpleNPC extends NPCMessage {
    private Integer nowBlood;
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

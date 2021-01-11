package com.liqihao.pojo.bean.bufferBean;

import com.liqihao.Cache.BufferMessageCache;
import com.liqihao.commons.enums.AttackStyleCode;
import com.liqihao.commons.enums.ConsumeTypeCode;
import com.liqihao.commons.enums.RoleStatusCode;
import com.liqihao.pojo.baseMessage.BufferMessage;
import com.liqihao.pojo.bean.roleBean.Role;
import com.liqihao.protobufObject.PlayModel;

/**
 * 影响血量buffer
 * @author lqhao
 */
public class HpBufferBean extends BaseBufferBean {
    private Integer flag;

    public Integer getFlag() {
        return flag;
    }

    public void setFlag(Integer flag) {
        this.flag = flag;
    }

    public HpBufferBean() {
    }

    public HpBufferBean(Integer flag) {
        this.flag = flag;
    }

    @Override
    public void effectToPeople(Role toRole){
        BufferMessage bufferMessage= BufferMessageCache.getInstance().get(getBufferMessageId());
        //扣血类型
        PlayModel.RoleIdDamage.Builder damageU = PlayModel.RoleIdDamage.newBuilder();
        damageU.setFromRoleId(getFromRoleId());
        damageU.setFromRoleType(getFromRoleType());
        damageU.setToRoleId(toRole.getId());
        damageU.setToRoleType(toRole.getType());
        damageU.setAttackStyle(AttackStyleCode.BUFFER.getCode());
        damageU.setBufferId(getBufferMessageId());
        damageU.setDamageType(ConsumeTypeCode.HP.getCode());
        damageU.setSkillId(-1);
        toRole.changeNowBlood(flag*bufferMessage.getBuffNum(),damageU,AttackStyleCode.BUFFER.getCode());
    }
    @Override
    public void effectToRole(Role toRole){
        BufferMessage bufferMessage= BufferMessageCache.getInstance().get(getBufferMessageId());
        toRole.hpRwLock.writeLock().lock();
        try {
            Integer hp = toRole.getNowHp() - bufferMessage.getBuffNum();
            if (hp <= 0) {
                hp = 0;
                toRole.setStatus(RoleStatusCode.DIE.getCode());
                toRole.die();
            }
            toRole.setNowHp(hp);
        }finally {
            toRole.hpRwLock.writeLock().unlock();
        }
        sendAllRoleDamage(toRole);
    }
}

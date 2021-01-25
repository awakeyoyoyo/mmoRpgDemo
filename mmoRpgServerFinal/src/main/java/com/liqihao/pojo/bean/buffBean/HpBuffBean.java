package com.liqihao.pojo.bean.buffBean;

import com.liqihao.Cache.BufferMessageCache;
import com.liqihao.commons.enums.AttackStyleCode;
import com.liqihao.commons.enums.ConsumeTypeCode;
import com.liqihao.commons.enums.DamageTypeCode;
import com.liqihao.commons.enums.RoleStatusCode;
import com.liqihao.pojo.baseMessage.BufferMessage;
import com.liqihao.pojo.bean.roleBean.Role;
import com.liqihao.protobufObject.PlayModel;

/**
 * 影响血量buffer
 * @author lqhao
 */
public class HpBuffBean extends BaseBuffBean {
    private Integer flag;

    public Integer getFlag() {
        return flag;
    }

    public void setFlag(Integer flag) {
        this.flag = flag;
    }

    public HpBuffBean() {
    }

    public HpBuffBean(Integer flag) {
        this.flag = flag;
    }

    @Override
    public void effectToPeople(Role toRole){
        BufferMessage bufferMessage= BufferMessageCache.getInstance().get(getBufferMessageId());
        //扣血类型
        PlayModel.RoleIdDamage.Builder builder = builderSimpleRoleDamage(toRole);
        builder.setAttackStyle(AttackStyleCode.BUFFER.getCode());
        builder.setDamageType(ConsumeTypeCode.HP.getCode());
        toRole.changeNowBlood(flag*bufferMessage.getBuffNum(),builder,AttackStyleCode.BUFFER.getCode());
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
        PlayModel.RoleIdDamage.Builder builder=builderRoleDamage(toRole);
        builder.setDamageType(DamageTypeCode.HP.getCode());
        sendAllRoleDamage(toRole,builder);
    }
}

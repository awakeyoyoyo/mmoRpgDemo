package com.liqihao.pojo.bean.buffBean;

import com.liqihao.Cache.BufferMessageCache;
import com.liqihao.commons.enums.AttackStyleCode;
import com.liqihao.commons.enums.ConsumeTypeCode;
import com.liqihao.commons.enums.DamageTypeCode;
import com.liqihao.pojo.baseMessage.BufferMessage;
import com.liqihao.pojo.bean.roleBean.Role;
import com.liqihao.protobufObject.PlayModel;

/**
 * 印象蓝量的buffer
 * @author lqhao
 */
public class MpBuffBean extends BaseBuffBean {
    /**
     * -1 1判断是扣除还是增加
     */
    private Integer flag;

    public Integer getFlag() {
        return flag;
    }

    public void setFlag(Integer flag) {
        this.flag = flag;
    }

    public MpBuffBean() {
    }

    public MpBuffBean(Integer flag) {
        this.flag = flag;
    }

    @Override
    public void effectToPeople(Role toRole){
        BufferMessage bufferMessage= BufferMessageCache.getInstance().get(getBufferMessageId());
        PlayModel.RoleIdDamage.Builder builder = builderSimpleRoleDamage(toRole);
        builder.setAttackStyle(AttackStyleCode.BUFFER.getCode());
        builder.setDamageType(ConsumeTypeCode.MP.getCode());
        toRole.changeMp(flag*bufferMessage.getBuffNum(), builder);
    }

    @Override
    public void effectToRole(Role toRole,Role fromRole){
        toRole.mpRwLock.writeLock().lock();
        try {
            BufferMessage bufferMessage= BufferMessageCache.getInstance().get(getBufferMessageId());
            int mp = toRole.getNowMp() - bufferMessage.getBuffNum();
            if (mp <= 0) {
                mp = 0;
            }
            toRole.setNowMp(mp);
        } finally {
            toRole.mpRwLock.writeLock().unlock();
        }
        PlayModel.RoleIdDamage.Builder builder=builderRoleDamage(toRole);
        builder.setDamageType(DamageTypeCode.MP.getCode());
        sendAllRoleDamage(toRole,builder);
    }
}

package com.liqihao.pojo.bean.articleBean;

import com.liqihao.Cache.ChannelMessageCache;
import com.liqihao.Cache.MediceneMessageCache;
import com.liqihao.Cache.SceneBeanMessageCache;
import com.liqihao.commons.ConstantValue;
import com.liqihao.commons.NettyResponse;
import com.liqihao.commons.StateCode;
import com.liqihao.commons.enums.*;
import com.liqihao.pojo.baseMessage.MedicineMessage;
import com.liqihao.pojo.bean.BackPackManager;
import com.liqihao.pojo.bean.roleBean.MmoSimpleRole;
import com.liqihao.pojo.dto.ArticleDto;
import com.liqihao.protobufObject.PlayModel;
import com.liqihao.util.ScheduledThreadPoolUtil;
import io.netty.channel.Channel;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * medicine bean
 *
 * @author lqhao
 */
public class MedicineBean  implements Article{
    /**
     * 药品基本信息Id
     */
    private Integer medicineMessageId;
    private Integer quantity;
    /**
     * 缓存中背包id
     */
    private Integer articleId;
    /**
     * 数据库行记录id
     */
    private Integer bagId;
    /**
     *
     *地面物品的下标
     */
    private Integer floorIndex;
    public Integer getFloorIndex() {
        return floorIndex;
    }
    public void setFloorIndex(Integer floorIndex) {
        this.floorIndex = floorIndex;
    }
    public Integer getBagId() {
        return bagId;
    }
    public void setBagId(Integer bagId) {
        this.bagId = bagId;
    }
    public Integer getMedicineMessageId() {
        return medicineMessageId;
    }
    public void setMedicineMessageId(Integer medicineMessageId) {
        this.medicineMessageId = medicineMessageId;
    }

    public Integer getArticleId() {
        return articleId;
    }

    public void setArticleId(Integer articleId) {
        this.articleId = articleId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    /**
     * 获取类型
     * @return
     */
    @Override
    public Integer getArticleTypeCode() {
        MedicineMessage medicineMessage= MediceneMessageCache.getInstance().get(getMedicineMessageId());
        return medicineMessage.getArticleType();
    }

    /**
     * 获取背包id
     * @return
     */
    @Override
    public Integer getArticleIdCode() {
        return getArticleId();
    }

    /**
     * 丢弃或者使用药品
     * @param number
     * @return
     */
    @Override
    public Article useOrAbandon(Integer number, BackPackManager backPackManager) {
        if (number <= getQuantity()) {
            //可以丢弃
            setQuantity(getQuantity() - number);
            //判断是否数量为0 为0则删除
            if (getQuantity() == 0) {
                //需要删除数据库的记录
                backPackManager.getNeedDeleteBagId().add(getBagId());
                setBagId(null);
                backPackManager.getBackpacks().remove(this);
                backPackManager.setNowSize(backPackManager.getNowSize()-1);
            }
            return this;
        } else {
            return null;
        }
    }
    /**
     * 物品转化为物品dto
     * @return
     */
    @Override
    public ArticleDto getArticleMessage() {
        ArticleDto articleDto = new ArticleDto();
        articleDto.setArticleId(getArticleId());
        articleDto.setId(getMedicineMessageId());
        articleDto.setArticleType(getArticleTypeCode());
        articleDto.setQuantity(getQuantity());
        articleDto.setBagId(getBagId());
        return articleDto;
    }

    @Override
    public <T extends Article> T getArticle() {
        return (T)this;
    }

    @Override
    public boolean put(BackPackManager backPackManager) {
        //查找背包中是否有
        List<Article> medicines = backPackManager.getBackpacks().stream()
                .filter(a -> a.getArticleTypeCode().equals(ArticleTypeCode.MEDICINE.getCode())).collect(Collectors.toList());
        //总数量
        Integer number = getQuantity();
        for (Article a : medicines) {
            MedicineBean temp = (MedicineBean) a;
            //物品类型
            if (getMedicineMessageId().equals(temp.getMedicineMessageId()) && number > 0) {
                //判断是否已经满了
                if (temp.getQuantity().equals(ConstantValue.BAG_MAX_VALUE)) {
                    continue;
                }
                Integer nowNum = temp.getQuantity();
                Integer sum = nowNum + number;
                //判断加上后是否已经超过99
                if (sum <= ConstantValue.BAG_MAX_VALUE) {
                    //不超过加上
                    temp.setQuantity(sum);
                    return true;
                } else {
                    number = number - (ConstantValue.BAG_MAX_VALUE - temp.getQuantity());
                    temp.setQuantity(ConstantValue.BAG_MAX_VALUE);
                }
            }
        }
        //表明背包中没有该物品或者该物品的数量都是99或者是剩余的 新建
        if (number != 0) {
            while (number > 0) {
                MedicineBean newMedicine = new MedicineBean();
                BeanUtils.copyProperties(this, newMedicine);
                if (number > ConstantValue.BAG_MAX_VALUE) {
                    newMedicine.setQuantity(ConstantValue.BAG_MAX_VALUE);
                    number -= ConstantValue.BAG_MAX_VALUE;
                } else {
                    newMedicine.setQuantity(number);
                    number = 0;
                }
                newMedicine.setArticleId(backPackManager.getNewArticleId());
                backPackManager.getBackpacks().add(newMedicine);
                backPackManager.setNowSize(backPackManager.getNowSize()+1);
            }
            return true;
        }
        return false;
    }

    @Override
    public void clearPut(BackPackManager backPackManager) {
        if (getBagId()!=null){
            backPackManager.getNeedDeleteBagId().add(getBagId());
        }
        setArticleId(backPackManager.getNewArticleId());
        setBagId(null);
        backPackManager.put(this);
    }

    @Override
    public boolean checkCanPut(BackPackManager backPackManager) {
        MedicineBean medicineBean = this;
        List<Article> medicines = backPackManager.getBackpacks().stream()
                .filter(a -> a.getArticleTypeCode().equals(ArticleTypeCode.MEDICINE.getCode())).collect(Collectors.toList());
        //总数量
        Integer number = medicineBean.getQuantity();
        for (Article a : medicines) {
            MedicineBean temp = (MedicineBean) a;
            //物品类型
            if (medicineBean.getMedicineMessageId().equals(temp.getMedicineMessageId()) && number > 0) {
                //判断是否已经满了
                if (temp.getQuantity().equals(backPackManager.getSize())) {
                    continue;
                }
                Integer nowNum = temp.getQuantity();
                Integer sum = nowNum + number;
                //判断加上后是否已经超过99
                if (sum <= backPackManager.getSize()) {
                    //不超过加上
                    return true;
                } else {
                    number = number - (backPackManager.getSize() - temp.getQuantity());
                }
            }
        }
        //表明背包中没有该物品或者该物品的数量都是99或者是剩余的 新建
        int gridNum = 0;
        if (number <= 0) {
            return true;
        }
        //生成新的格子
        while (number > 0) {
            if (number > ConstantValue.BAG_MAX_VALUE) {
                number -= ConstantValue.BAG_MAX_VALUE;
            } else {
                number = 0;
            }
            gridNum++;
        }
        if (backPackManager.getNowSize() + gridNum <= backPackManager.getSize()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean use(BackPackManager backpackManager, MmoSimpleRole mmoSimpleRole) {
        backpackManager.useOrAbandonArticle(articleId, 1);
        //判断是瞬间恢复还是持续性恢复
        MedicineMessage medicineMessage= MediceneMessageCache.getInstance().get(getMedicineMessageId());
        if (medicineMessage.getMedicineType().equals(MedicineTypeCode.MOMENT.getCode())){
            Integer addNumber=medicineMessage.getDamageValue();
            if (medicineMessage.getDamageType().equals(DamageTypeCode.MP.getCode())) {
                Integer oldMp = mmoSimpleRole.getNowMp();
                Integer newNumber = oldMp + addNumber;
                if (newNumber > mmoSimpleRole.getMp()) {
                    mmoSimpleRole.setNowMp(mmoSimpleRole.getMp());
                    addNumber = mmoSimpleRole.getMp() - oldMp;
                } else {
                    mmoSimpleRole.setNowMp(newNumber);
                }
            }else{
                Integer oldHP = mmoSimpleRole.getNowHp();
                Integer newNumber = oldHP + addNumber;
                if (newNumber > mmoSimpleRole.getHp()) {
                    mmoSimpleRole.setNowHp(mmoSimpleRole.getHp());
                    addNumber = mmoSimpleRole.getHp() - oldHP;
                    //发送数据包
                } else {
                    mmoSimpleRole.setNowHp(newNumber);
                }
            }
            PlayModel.RoleIdDamage.Builder damageU = PlayModel.RoleIdDamage.newBuilder();
            damageU.setFromRoleId(mmoSimpleRole.getId());
            damageU.setToRoleId(mmoSimpleRole.getId());
            damageU.setAttackStyle(AttackStyleCode.MEDICINE.getCode());
            damageU.setBufferId(-1);
            damageU.setDamage(addNumber);
            damageU.setDamageType(medicineMessage.getDamageType());
            damageU.setMp(mmoSimpleRole.getNowMp());
            damageU.setNowblood(mmoSimpleRole.getNowHp());
            damageU.setSkillId(-1);
            damageU.setState(mmoSimpleRole.getStatus());
            PlayModel.PlayModelMessage.Builder myMessageBuilder = PlayModel.PlayModelMessage.newBuilder();
            myMessageBuilder.setDataType(PlayModel.PlayModelMessage.DateType.DamagesNoticeResponse);
            PlayModel.DamagesNoticeResponse.Builder damagesNoticeBuilder = PlayModel.DamagesNoticeResponse.newBuilder();
            damagesNoticeBuilder.setRoleIdDamage(damageU);
            myMessageBuilder.setDamagesNoticeResponse(damagesNoticeBuilder.build());
            NettyResponse nettyResponse = new NettyResponse();
            nettyResponse.setCmd(ConstantValue.DAMAGES_NOTICE_RESPONSE);
            nettyResponse.setStateCode(StateCode.SUCCESS);
            nettyResponse.setData(myMessageBuilder.build().toByteArray());

            Integer sceneId = mmoSimpleRole.getMmoSceneId();
            List<Integer> players = SceneBeanMessageCache.getInstance().get(sceneId).getRoles();
            for (Integer playerId : players) {
                Channel cc = ChannelMessageCache.getInstance().get(playerId);
                if (cc != null) {
                    cc.writeAndFlush(nettyResponse);
                }
            }
            return  true;
        }else {
            //判断是否已经有持续性药品任务
            ConcurrentHashMap<String, ScheduledFuture<?>> replyMpRoleMap = ScheduledThreadPoolUtil.getReplyMpRole();
            String key = mmoSimpleRole.getId().toString();
            if (medicineMessage.getDamageType().equals(DamageTypeCode.MP.getCode())) {
                key = key + "MP";
            } else {
                key = key + "HP";
            }
            if (!replyMpRoleMap.containsKey(key)) {
                //传入每秒恢复量
                Integer lastTime=medicineMessage.getLastTime();
                Integer secondValue=medicineMessage.getSecondValue();
                ScheduledThreadPoolUtil.ReplyMpTask replyMpTask = new ScheduledThreadPoolUtil.ReplyMpTask(mmoSimpleRole, secondValue, medicineMessage.getDamageType(), key,lastTime);
                // 周期性执行，每3秒执行一次
                ScheduledFuture<?> t = ScheduledThreadPoolUtil.getScheduledExecutorService().scheduleAtFixedRate(replyMpTask, 0, 1, TimeUnit.SECONDS);
                replyMpRoleMap.put(key, t);
                return true;
            } else {
                //已经有持续性恢复药品在使用 无法再使用
                return false;
            }
        }
    }
}
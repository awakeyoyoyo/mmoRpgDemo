package com.liqihao.pojo.baseMessage;

/**
 * 副本基本信息
 * @author lqhao
 */
public class CopySceneMessage extends BaseMessage{
    private Integer id;
    private String name;
    private Integer lastTime;
    private String bossIds;
    /**
     * 可掉落药品ids集合
     */
    private String medicineIds;
    /**
     * 可掉落装备ids集合
     */
    private String equipmentIds;
    /**
     * 挑战成功每人所得金币
     */
    private Integer money;

    @Override
    public Integer getTheId() {
        return getId();
    }

    public String getMedicineIds() {
        return medicineIds;
    }

    public String getEquipmentIds() {
        return equipmentIds;
    }

    public Integer getMoney() {
        return money;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getLastTime() {
        return lastTime;
    }

    public String getBossIds() {
        return bossIds;
    }
}

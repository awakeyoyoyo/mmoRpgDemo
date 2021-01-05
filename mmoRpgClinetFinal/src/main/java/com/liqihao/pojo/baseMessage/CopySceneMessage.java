package com.liqihao.pojo.baseMessage;

/**
 * 副本基本信息
 * @author lqhao
 */
public class CopySceneMessage {
    private Integer id;
    private String name;
    private Integer lastTime;
    private String bossIds;
    private String medicineIds;
    private String equipmentIds;
    private Integer money;

    public String getMedicineIds() {
        return medicineIds;
    }

    public void setMedicineIds(String medicineIds) {
        this.medicineIds = medicineIds;
    }

    public String getEquipmentIds() {
        return equipmentIds;
    }

    public void setEquipmentIds(String equipmentIds) {
        this.equipmentIds = equipmentIds;
    }

    public Integer getMoney() {
        return money;
    }

    public void setMoney(Integer money) {
        this.money = money;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getLastTime() {
        return lastTime;
    }

    public void setLastTime(Integer lastTime) {
        this.lastTime = lastTime;
    }

    public String getBossIds() {
        return bossIds;
    }

    public void setBossIds(String bossIds) {
        this.bossIds = bossIds;
    }
}

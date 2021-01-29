package com.liqihao.pojo.dto;

/**
 * 传输装备栏装备dto
 * @author lqhao
 */
public class EquipmentDto {
    /**
     * 物品id
     */

    private Integer id;
    /**
     *   部位
     */

    private Integer position;
    /**
     *  当前耐久度
     */

    private Integer nowDurability;
    /**
     *   装备栏id
     */

    private Integer equipmentBagId;
    /**
     * 装备数据库id
     */
    private Integer equipmentId;
    /**
     * 装备星级
     */
    private Integer equipmentLevel;

    public Integer getEquipmentLevel() {
        return equipmentLevel;
    }

    public void setEquipmentLevel(Integer equipmentLevel) {
        this.equipmentLevel = equipmentLevel;
    }

    public Integer getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(Integer equipmentId) {
        this.equipmentId = equipmentId;
    }

    public Integer getEquipmentBagId() {
        return equipmentBagId;
    }

    public void setEquipmentBagId(Integer equipmentBagId) {
        this.equipmentBagId = equipmentBagId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public Integer getNowDurability() {
        return nowDurability;
    }

    public void setNowDurability(Integer nowDurability) {
        this.nowDurability = nowDurability;
    }
}

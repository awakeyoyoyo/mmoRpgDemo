package com.liqihao.pojo.dto;

import java.util.ArrayList;

/**
 * boss传输类
 * @author lqhao
 */
public class BossBeanDto {
    private Integer nowBlood;
    private Integer nowMp;
    private Integer roleStatus;
    private Integer id;
    private String name;
    private Integer blood;
    private Integer mp;
    private Integer attack;
    private double damageAdd;
    private ArrayList<BufferDto> bufferDtos;
    private Integer status;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public ArrayList<BufferDto> getBufferDtos() {
        return bufferDtos;
    }

    public void setBufferDtos(ArrayList<BufferDto> bufferDtos) {
        this.bufferDtos = bufferDtos;
    }

    public Integer getNowBlood() {
        return nowBlood;
    }

    public void setNowBlood(Integer nowBlood) {
        this.nowBlood = nowBlood;
    }

    public Integer getNowMp() {
        return nowMp;
    }

    public void setNowMp(Integer nowMp) {
        this.nowMp = nowMp;
    }

    public Integer getRoleStatus() {
        return roleStatus;
    }

    public void setRoleStatus(Integer roleStatus) {
        this.roleStatus = roleStatus;
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

    public Integer getBlood() {
        return blood;
    }

    public void setBlood(Integer blood) {
        this.blood = blood;
    }

    public Integer getMp() {
        return mp;
    }

    public void setMp(Integer mp) {
        this.mp = mp;
    }

    public Integer getAttack() {
        return attack;
    }

    public void setAttack(Integer attack) {
        this.attack = attack;
    }

    public double getDamageAdd() {
        return damageAdd;
    }

    public void setDamageAdd(double damageAdd) {
        this.damageAdd = damageAdd;
    }

}

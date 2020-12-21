package com.liqihao.pojo.baseMessage;

public class NPCMessage {
    private Integer id;
    private String name;
    private Integer mmosceneid;
    private Integer status;
    private Integer type;
    private Integer onstatus;
    private String talk;
    private Integer blood;
    private Integer mp;
    private Integer attack;
    private double damageAdd;

    public double getDamageAdd() {
        return damageAdd;
    }

    public void setDamageAdd(double damageAdd) {
        this.damageAdd = damageAdd;
    }

    public Integer getAttack() {
        return attack;
    }

    public void setAttack(Integer attack) {
        this.attack = attack;
    }

    public Integer getMp() {
        return mp;
    }

    public void setMp(Integer mp) {
        this.mp = mp;
    }

    @Override
    public String toString() {
        return "NPCMessage{" +
                "id=" + id +
                ", status=" + status +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", mmosceneid=" + mmosceneid +
                ", onstatus=" + onstatus +
                ", talk='" + talk + '\'' +
                ", blood=" + blood +
                '}';
    }

    public String getTalk() {
        return talk;
    }

    public void setTalk(String talk) {
        this.talk = talk;
    }

    public Integer getBlood() {
        return blood;
    }

    public void setBlood(Integer blood) {
        this.blood = blood;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getMmosceneid() {
        return mmosceneid;
    }

    public void setMmosceneid(Integer mmosceneid) {
        this.mmosceneid = mmosceneid;
    }

    public Integer getOnstatus() {
        return onstatus;
    }

    public void setOnstatus(Integer onstatus) {
        this.onstatus = onstatus;
    }
}

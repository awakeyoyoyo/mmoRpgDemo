package com.liqihao.pojo.baseMessage;

/**
 * npc信息
 * @author lqhao
 */
public class NpcMessage extends BaseMessage{
    private Integer id;
    private String name;
    private Integer mmoSceneId;
    private Integer status;
    private Integer type;
    private Integer onStatus;
    private String talk;
    private Integer blood;
    private Integer mp;
    private Integer attack;
    private double damageAdd;
    private Integer addExp;

    public Integer getAddExp() {
        return addExp;
    }

    public void setAddExp(Integer addExp) {
        this.addExp = addExp;
    }

    @Override
    public Integer getTheId() {
        return getId();
    }
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
        return "NpcMessage{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", mmoSceneId=" + mmoSceneId +
                ", status=" + status +
                ", type=" + type +
                ", onStatus=" + onStatus +
                ", talk='" + talk + '\'' +
                ", blood=" + blood +
                ", mp=" + mp +
                ", attack=" + attack +
                ", damageAdd=" + damageAdd +
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

    public Integer getMmoSceneId() {
        return mmoSceneId;
    }

    public void setMmoSceneId(Integer mmoSceneId) {
        this.mmoSceneId = mmoSceneId;
    }

    public Integer getOnStatus() {
        return onStatus;
    }

    public void setOnStatus(Integer onStatus) {
        this.onStatus = onStatus;
    }
}

package com.liqihao.pojo.baseMessage;

public class NPCMessage {
    private Integer id;

    private Integer status;

    private String name;

    private Integer type;

    private Integer mmosceneid;

    private Integer onstatus;

    @Override
    public String toString() {
        return "NPCMessage{" +
                "id=" + id +
                ", status=" + status +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", mmosceneid=" + mmosceneid +
                ", onstatus=" + onstatus +
                '}';
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

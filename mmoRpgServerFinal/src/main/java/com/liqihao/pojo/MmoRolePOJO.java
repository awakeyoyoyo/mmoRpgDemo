package com.liqihao.pojo;

public class MmoRolePOJO {
    private Integer id;

    private Integer status;

    private String name;

    private Integer type;

    private Integer mmosceneid;

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
        this.name = name == null ? null : name.trim();
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
}
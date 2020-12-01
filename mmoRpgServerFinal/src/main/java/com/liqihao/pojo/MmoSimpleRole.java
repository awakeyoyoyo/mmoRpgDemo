package com.liqihao.pojo;

public class MmoSimpleRole {
    private int id;
    private String name;
    private String status;
    private String type;
    private String onstatus;

    public String getOnstatus() {
        return onstatus;
    }
    public void setOnstatus(String onstatus) {
        this.onstatus = onstatus;
    }
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "MmoSimpleRole{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", status='" + status + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}

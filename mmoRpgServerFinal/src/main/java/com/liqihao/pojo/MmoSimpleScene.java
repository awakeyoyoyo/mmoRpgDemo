package com.liqihao.pojo;

public class MmoSimpleScene {
    private int id;
    private String palceName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPalceName() {
        return palceName;
    }

    public void setPalceName(String palceName) {
        this.palceName = palceName;
    }

    @Override
    public String toString() {
        return "MmoSimpleScene{" +
                "id=" + id +
                ", palceName='" + palceName + '\'' +
                '}';
    }
}

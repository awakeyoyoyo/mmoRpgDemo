package com.liqihao.codec;

import java.io.Serializable;

public class MmoPerson implements Serializable {

    private static final long serialVersionUID = 8213761673743652497L;

    private Integer id;

    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "MmoPerson{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }
}
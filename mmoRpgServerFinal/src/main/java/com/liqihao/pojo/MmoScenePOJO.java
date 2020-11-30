package com.liqihao.pojo;

public class MmoScenePOJO {
    private Integer id;

    private String placename;

    private String canscene;

    private String roles;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPlacename() {
        return placename;
    }

    public void setPlacename(String placename) {
        this.placename = placename == null ? null : placename.trim();
    }

    public String getCanscene() {
        return canscene;
    }

    public void setCanscene(String canscene) {
        this.canscene = canscene == null ? null : canscene.trim();
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles == null ? null : roles.trim();
    }
}
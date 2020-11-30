package com.liqihao.pojo;

import java.util.List;

public class MmoScene {
    private int id;
    private String placeName;
    private List<MmoSimpleScene> canScene;
    private List<MmoSimpleRole> roles;

    //打印出当前场景上的角色
    public String printRoles(){
        StringBuilder stringBuilder=new StringBuilder();
        for (MmoSimpleRole mmoSimpleRole:roles){
            stringBuilder.append(mmoSimpleRole.getName()+" ");
        }
        return stringBuilder.toString();
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public List<MmoSimpleScene> getCanScene() {
        return canScene;
    }

    public void setCanScene(List<MmoSimpleScene> canScene) {
        this.canScene = canScene;
    }

    public List<MmoSimpleRole> getRoles() {
        return roles;
    }

    public void setRoles(List<MmoSimpleRole> roles) {
        this.roles = roles;
    }

    @Override
    public String toString() {
        return "MmoScene{" +
                "id=" + id +
                ", placeName='" + placeName + '\'' +
                ", canScene=" + canScene +
                ", roles=" + roles +
                '}';
    }
}

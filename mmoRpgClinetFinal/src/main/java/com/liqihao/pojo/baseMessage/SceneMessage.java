package com.liqihao.pojo.baseMessage;

public class SceneMessage {
    private Integer id;

    private String placeName;

    private String canScene;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getCanScene() {
        return canScene;
    }

    public void setCanScene(String canScene) {
        this.canScene = canScene;
    }

    @Override
    public String toString() {
        return "SceneMessage{" +
                "id=" + id +
                ", placeName='" + placeName + '\'' +
                ", canScene='" + canScene + '\'' +
                '}';
    }
}

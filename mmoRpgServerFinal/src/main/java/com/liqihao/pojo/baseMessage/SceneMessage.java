package com.liqihao.pojo.baseMessage;
/**
 * 场景信息类
 * @author lqhao
 */
public class SceneMessage extends BaseMessage{
    private Integer id;

    private String placeName;

    private String canScene;
    @Override
    public Integer getTheId() {
        return getId();
    }

    public Integer getId() {
        return id;
    }

    public String getPlaceName() {
        return placeName;
    }

    public String getCanScene() {
        return canScene;
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

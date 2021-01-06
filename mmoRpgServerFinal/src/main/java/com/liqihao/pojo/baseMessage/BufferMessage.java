package com.liqihao.pojo.baseMessage;

/**
 * buffer信息
 * @author Administrator
 */
public class BufferMessage {
    private Integer id;
    private String name;
    private Integer buffType;
    private Integer buffNum;
    private Integer lastTime ;
    private Integer spaceTime ;
    private Integer bufferStyle;

    public Integer getBufferStyle() {
        return bufferStyle;
    }

    public void setBufferStyle(Integer bufferStyle) {
        this.bufferStyle = bufferStyle;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getBuffType() {
        return buffType;
    }

    public void setBuffType(Integer buffType) {
        this.buffType = buffType;
    }

    public Integer getBuffNum() {
        return buffNum;
    }

    public void setBuffNum(Integer buffNum) {
        this.buffNum = buffNum;
    }

    public Integer getLastTime() {
        return lastTime;
    }

    public void setLastTime(Integer lastTime) {
        this.lastTime = lastTime;
    }

    public Integer getSpaceTime() {
        return spaceTime;
    }

    public void setSpaceTime(Integer spaceTime) {
        this.spaceTime = spaceTime;
    }
}

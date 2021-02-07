package com.liqihao.pojo.baseMessage;

/**
 * buffer信息
 * @author Administrator
 */
public class BufferMessage extends BaseMessage {
    private Integer id;
    private String name;
    private Integer buffType;
    private Integer buffNum;
    private Integer lastTime ;
    private Integer spaceTime ;
    private Integer bufferStyle;
    @Override
    public Integer getTheId() {
        return getId();
    }
    public Integer getBufferStyle() {
        return bufferStyle;
    }

    private void setBufferStyle(Integer bufferStyle) {
        this.bufferStyle = bufferStyle;
    }

    public Integer getId() {
        return id;
    }

    private void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    private void setName(String name) {
        this.name = name;
    }

    public Integer getBuffType() {
        return buffType;
    }

    private void setBuffType(Integer buffType) {
        this.buffType = buffType;
    }

    public Integer getBuffNum() {
        return buffNum;
    }

    private void setBuffNum(Integer buffNum) {
        this.buffNum = buffNum;
    }

    public Integer getLastTime() {
        return lastTime;
    }

    private void setLastTime(Integer lastTime) {
        this.lastTime = lastTime;
    }

    public Integer getSpaceTime() {
        return spaceTime;
    }

    private void setSpaceTime(Integer spaceTime) {
        this.spaceTime = spaceTime;
    }
}

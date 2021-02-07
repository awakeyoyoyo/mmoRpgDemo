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

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getBuffType() {
        return buffType;
    }

    public Integer getBuffNum() {
        return buffNum;
    }

    public Integer getLastTime() {
        return lastTime;
    }

    public Integer getSpaceTime() {
        return spaceTime;
    }
}

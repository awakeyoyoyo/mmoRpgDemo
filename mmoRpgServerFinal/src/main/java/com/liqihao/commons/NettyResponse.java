package com.liqihao.commons;

/**
 * 响应
 * @author lqhao
 */
public class NettyResponse {
    /**
     * 状态码
     */
    private int stateCode;
    /**
     * 请求命令
     */
    private int cmd;
    /**
     * 数据部分
     */
    private byte[] data;

    public NettyResponse() {
    }

    public NettyResponse(int stateCode, int cmd, byte[] data) {
        this.stateCode = stateCode;
        this.cmd = cmd;
        this.data = data;
    }

    public int getStateCode() {
        return stateCode;
    }

    public void setStateCode(int stateCode) {
        this.stateCode = stateCode;
    }

    public int getCmd() {
        return cmd;
    }

    public void setCmd(int cmd) {
        this.cmd = cmd;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public int getDataLength(){
        if (data==null){
            return 0;
        }
        return data.length;
    }
}

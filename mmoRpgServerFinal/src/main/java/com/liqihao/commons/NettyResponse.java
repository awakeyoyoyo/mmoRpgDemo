package com.liqihao.commons;

public class NettyResponse {
    /**
     * 状态码
     */
    private int stateCode;
    /**
     * 请求模块
     */
    private short module;
    /**
     * 请求命令
     */
    private short cmd;
    /**
     * 数据部分
     */
    private byte[] data;

    public int getStateCode() {
        return stateCode;
    }

    public void setStateCode(int stateCode) {
        this.stateCode = stateCode;
    }

    public short getModule() {
        return module;
    }

    public void setModule(short module) {
        this.module = module;
    }

    public short getCmd() {
        return cmd;
    }

    public void setCmd(short cmd) {
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

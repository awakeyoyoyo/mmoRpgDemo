package com.liqihao.commons;

public class NettyRequest {
    /**
     * 请求命令
     */
    private int cmd;
    /**
     * 数据部分
     */
    private byte[] data;

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
